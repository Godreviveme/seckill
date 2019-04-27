package com.xiaolong.miaosha.controller;

import com.xiaolong.miaosha.domain.User;
import com.xiaolong.miaosha.redis.RedisService;
import com.xiaolong.miaosha.redis.Userkey;
import com.xiaolong.miaosha.result.Result;
import com.xiaolong.miaosha.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;

    @GetMapping("/thymeleaf")
    public String thymeleaf(Model model) {
        model.addAttribute("name","xiaolong");
        return "hello";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet() {
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx() {
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet() {
        User user = redisService.get(Userkey.getById,"key1", User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {
        User user = new User(1, "1111");
        redisService.set(Userkey.getById,"1", user);
        return Result.success(true);
    }

}
