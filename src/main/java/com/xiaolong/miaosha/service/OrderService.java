package com.xiaolong.miaosha.service;

import com.xiaolong.miaosha.dao.OrderDao;
import com.xiaolong.miaosha.domain.MiaoshaOrder;
import com.xiaolong.miaosha.domain.MiaoshaUser;
import com.xiaolong.miaosha.domain.OrderInfo;
import com.xiaolong.miaosha.redis.OrderKey;
import com.xiaolong.miaosha.redis.RedisService;
import com.xiaolong.miaosha.vo.GoodsVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;


    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userId, long goodsId) {
//        return orderDao.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);

        return redisService.get(OrderKey.getMiaoshaOrderByUidGid, "", MiaoshaOrder.class);
    }

    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(user.getId());
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setCreateDate(new Date());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);

        orderDao.insert(orderInfo);

        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        redisService.set(OrderKey.getMiaoshaOrderByUidGid, "", miaoshaOrder);
        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }

    public void deleteOrders() {
        orderDao.deleteOrders();
        orderDao.deleteMiaoshaOrders();
    }
}
