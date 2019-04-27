package com.xiaolong.miaosha.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MiaoshaGoods {

    private Long id;
    private Long goodsId;
    private Integer stockCount;
    private Date startDate;
}
