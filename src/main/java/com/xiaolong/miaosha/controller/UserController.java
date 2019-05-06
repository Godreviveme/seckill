package com.xiaolong.miaosha.controller;

import com.xiaolong.miaosha.domain.MiaoshaUser;
import com.xiaolong.miaosha.redis.RedisService;
import com.xiaolong.miaosha.result.Result;
import com.xiaolong.miaosha.service.MiaoshaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    MiaoshaService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(Model model, MiaoshaUser user) {
        return Result.success(user);
    }



}
