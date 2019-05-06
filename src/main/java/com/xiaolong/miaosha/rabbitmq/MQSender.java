package com.xiaolong.miaosha.rabbitmq;

import com.xiaolong.miaosha.redis.RedisService;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;
//
//    public void send(Object message) {
//        String msg = RedisService.beanToString(message);
//        log.info("Send message: {}", msg);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
//    }
//
//
//    public void sendTopic(Object message) {
//        String msg = RedisService.beanToString(message);
//        log.info("Send topic message: {}", msg);
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + "1");
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + "2");
//    }
//
//    public void sendFanout(Object message) {
//        String msg = RedisService.beanToString(message);
//        log.info("Send fanout message: {}", msg);
//        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",  msg);
//    }
//
//    public void sendHeaders(Object message) {
//        String msg = RedisService.beanToString(message);
//        log.info("Send headers message: {}", msg);
//        MessageProperties properties = new MessageProperties();
//        properties.setHeader("header1", "value1");
//        properties.setHeader("header2", "value2");
//        Message obj = new Message(msg.getBytes(), properties);
//        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,"", obj);
//    }

    public void sendMiaoshaMessage(MiaoshaMessage message) {
        String msg = RedisService.beanToString(message);
        log.info("Send message: {}", msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
    }
}
