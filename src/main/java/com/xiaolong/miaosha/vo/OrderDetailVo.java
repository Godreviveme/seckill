package com.xiaolong.miaosha.vo;

import com.xiaolong.miaosha.domain.OrderInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVo {

    private GoodsVo goods;
    private OrderInfo order;
}
