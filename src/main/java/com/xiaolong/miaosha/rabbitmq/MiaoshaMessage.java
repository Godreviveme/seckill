package com.xiaolong.miaosha.rabbitmq;

import com.xiaolong.miaosha.domain.MiaoshaUser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MiaoshaMessage {

    private MiaoshaUser user;
    private long goodsId;
}
