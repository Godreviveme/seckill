package com.xiaolong.miaosha.controller;

import com.xiaolong.miaosha.domain.MiaoshaUser;
import com.xiaolong.miaosha.redis.GoodsKey;
import com.xiaolong.miaosha.redis.RedisService;
import com.xiaolong.miaosha.result.Result;
import com.xiaolong.miaosha.service.GoodsService;
import com.xiaolong.miaosha.service.MiaoshaUserService;
import com.xiaolong.miaosha.vo.GoodsDetailVo;
import com.xiaolong.miaosha.vo.GoodsVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;

    /**
     *
     * After: 5000, QPS 50
     */
    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String toList(Model model, MiaoshaUser user, HttpServletRequest request,
                         HttpServletResponse response) {
        String html  = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsList);
        WebContext context = new WebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", context);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "",html);
        }
        return html;

    }


    @RequestMapping(value = "/to_detail2/{goodsId}", produces = "text/html")
    @ResponseBody
    public String detail2(Model model, MiaoshaUser user, @PathVariable("goodsId") long goodsId,
            HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("user", user);
        GoodsVo goodsVo =  goodsService.getGoodsVoByGoodsId(goodsId);

        long startAt = goodsVo.getStartDate().getTime();
        long endAt = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus;
        long remainSeconds;
        if (now < startAt) {
            miaoshaStatus = 0;
            remainSeconds = (endAt - now) / 1000;
        } else if (now > endAt) {
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("goods",goodsVo);
//        return "goods_detail";
        //        return "goods_list";
        String html  = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        SpringWebContext context = new SpringWebContext(request, response, request.getServletContext(),
                request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", context);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail, "" + goodsId, html);
        }
        return html;

    }


    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(MiaoshaUser user, @PathVariable("goodsId") long goodsId,
            HttpServletRequest request, HttpServletResponse response) {
        GoodsVo goodsVo =  goodsService.getGoodsVoByGoodsId(goodsId);

        long startAt = goodsVo.getStartDate().getTime();
        long endAt = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();

        GoodsDetailVo vo = new GoodsDetailVo();

        int miaoshaStatus;
        int remainSeconds;
        if (now < startAt) {
            miaoshaStatus = 0;
            remainSeconds = (int)(endAt - now) / 1000;
        } else if (now > endAt) {
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        vo.setGoods(goodsVo);
        vo.setMiaoshaStatus(miaoshaStatus);
        vo.setRemainSeconds(remainSeconds);
        return Result.success(vo);

    }

}
