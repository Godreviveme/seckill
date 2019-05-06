package com.xiaolong.miaosha.rabbitmq;

import com.xiaolong.miaosha.dao.GoodsDao;
import com.xiaolong.miaosha.domain.MiaoshaOrder;
import com.xiaolong.miaosha.domain.MiaoshaUser;
import com.xiaolong.miaosha.domain.OrderInfo;
import com.xiaolong.miaosha.redis.RedisService;
import com.xiaolong.miaosha.result.CodeMsg;
import com.xiaolong.miaosha.result.Result;
import com.xiaolong.miaosha.service.GoodsService;
import com.xiaolong.miaosha.service.MiaoshaService;
import com.xiaolong.miaosha.service.OrderService;
import com.xiaolong.miaosha.vo.GoodsVo;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    GoodsService goodsService;

    @Autowired OrderService orderService;

    @Autowired
    RedisService redisService;

    @Autowired MiaoshaService miaoshaService;

//    @RabbitListener(queues = MQConfig.QUEUE)
//    public void receive(String message) {
//        log.info("receive message: {}", message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//    public void receiveTopic1(String message) {
//        log.info("receive queue1 message: {}", message);
//    }
//
//    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//    public void receiveTopic2(String message) {
//        log.info("receive queue2 message: {}", message);
//    }
//
//    @RabbitListener(queues = MQConfig.HEADERS_QUEUE)
//    public void receiveHeaderQueue(byte[] message) {
//        log.info("receive header queue message: {}", new String(message));
//    }

        @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
        public void receive(String message) {
            log.info("receive message: {}", message);
            MiaoshaMessage mm = RedisService.stringToBean(message, MiaoshaMessage.class);
            MiaoshaUser user = mm.getUser();
            long goodsId = mm.getGoodsId();

            GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
            int stock = goods.getGoodsStock();
            if (stock <= 0) {
                return;
            }

            //判断是否曾经秒杀过
            MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
            if (order != null) {
                return;
            }
            miaoshaService.miaosha(user, goods);
        }

}
