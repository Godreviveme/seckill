package com.xiaolong.miaosha.service;

import com.xiaolong.miaosha.domain.MiaoshaOrder;
import com.xiaolong.miaosha.domain.MiaoshaUser;
import com.xiaolong.miaosha.domain.OrderInfo;
import com.xiaolong.miaosha.redis.MiaoshaKey;
import com.xiaolong.miaosha.redis.RedisService;
import com.xiaolong.miaosha.vo.GoodsVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    public void reset(List<GoodsVo> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        boolean success = goodsService.reduceStock(goods);
        if (success) {
            return orderService.createOrder(user, goods);
        } else {
            //已经卖完
            setGoodsOver(goods.getId());
            return null;
        }
    }


    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
        if (order != null) {
            return order.getOrderId();
        } else {
            Boolean over = getGoodsOver(goodsId);
            return (over != null && over) ? -1 : 0;
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, "" + goodsId, true);
    }

    private Boolean getGoodsOver(long goodsId) {
        return redisService.get(MiaoshaKey.isGoodsOver, "" + goodsId, Boolean.class);
    }
}
