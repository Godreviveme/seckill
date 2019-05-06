package com.xiaolong.miaosha.controller;

import com.xiaolong.miaosha.domain.MiaoshaOrder;
import com.xiaolong.miaosha.domain.MiaoshaUser;
import com.xiaolong.miaosha.domain.OrderInfo;
import com.xiaolong.miaosha.rabbitmq.MQSender;
import com.xiaolong.miaosha.rabbitmq.MiaoshaMessage;
import com.xiaolong.miaosha.redis.GoodsKey;
import com.xiaolong.miaosha.redis.MiaoshaKey;
import com.xiaolong.miaosha.redis.OrderKey;
import com.xiaolong.miaosha.redis.RedisService;
import com.xiaolong.miaosha.result.CodeMsg;
import com.xiaolong.miaosha.result.Result;
import com.xiaolong.miaosha.service.GoodsService;
import com.xiaolong.miaosha.service.MiaoshaService;
import com.xiaolong.miaosha.service.OrderService;
import com.xiaolong.miaosha.vo.GoodsVo;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/miaosha")
@Slf4j
public class MiaoshaController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    MQSender sender;

    private Map<Long, Boolean> localOverMap = new HashMap<>();

    /**
     * 系统初始化
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if (goodsVoList == null) {
            return;
        }
        for (GoodsVo goodsVo : goodsVoList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock, goodsVo.getId() + "", goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(), false);
        }

    }

    /**
     * orderId : 秒杀成功
     * -1 ：秒杀失败
     * 0 ： 排队中
     */

    @GetMapping("/result")
    @ResponseBody
    public Result miaoshaResult(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);

    }

    /**
     * 优化之前
     * 并发3000
     * QPS 34
     *
     * 优化之后
     * 并发8000
     * QPS 300
     */

    @GetMapping(value = "/do_miaosha")
    @ResponseBody
    public Result list(Model model, MiaoshaUser user,
            @RequestParam("goodsId") long goodsId) {
        log.info("start do miaosha, goodsId={}", goodsId);
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }

        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //预减库存
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, goodsId + "");
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //判断是否重复秒杀
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //入队
        MiaoshaMessage message = new MiaoshaMessage(user, goodsId);
        sender.sendMiaoshaMessage(message);
        return Result.success(0);

        /*
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getGoodsStock();
        if (stock <= 0) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        model.addAttribute("goods", goods);
        //判断是否曾经秒杀过
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        return Result.success(orderInfo);
        */
    }

    @GetMapping(value="/reset")
    @ResponseBody
    public Result<Boolean> reset(Model model) {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for(GoodsVo goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), 10);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
        redisService.delete(MiaoshaKey.isGoodsOver);
        miaoshaService.reset(goodsList);
        return Result.success(true);
    }
}
