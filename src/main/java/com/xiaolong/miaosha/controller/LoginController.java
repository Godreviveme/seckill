package com.xiaolong.miaosha.controller;

import com.xiaolong.miaosha.redis.RedisService;
import com.xiaolong.miaosha.result.CodeMsg;
import com.xiaolong.miaosha.result.Result;
import com.xiaolong.miaosha.service.MiaoshaUserService;
import com.xiaolong.miaosha.vo.LoginVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());
        userService.login(response, loginVo);
        return Result.success(true);
    }

}
