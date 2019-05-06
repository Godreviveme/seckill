package com.xiaolong.miaosha.redis;

public class MiaoshaUserkey extends BasePrefix{

    public static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    public MiaoshaUserkey(String prefix) {
        super(prefix);
    }

    public MiaoshaUserkey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaUserkey token = new MiaoshaUserkey(TOKEN_EXPIRE, "tk");
    public static MiaoshaUserkey getById = new MiaoshaUserkey(0, "tk");
}
