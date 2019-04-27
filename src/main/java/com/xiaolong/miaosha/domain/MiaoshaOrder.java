package com.xiaolong.miaosha.domain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MiaoshaOrder {

    private Long id;
    private Long userId;
    private Long  orderId;
    private Long goodsId;

}
