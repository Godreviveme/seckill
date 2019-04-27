package com.xiaolong.miaosha.redis;

public interface KeyPrefix {

    int expireSeconds();

    String getPrefix();
}
