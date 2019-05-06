package com.xiaolong.miaosha.redis;

public class MiaoshaKey extends BasePrefix {

    public MiaoshaKey(String prefix) {
        super(prefix);
    }

    public static final MiaoshaKey isGoodsOver = new MiaoshaKey("go");
}
