package com.xiaolong.miaosha.service;

import com.xiaolong.miaosha.dao.MiaoshaUserDao;
import com.xiaolong.miaosha.domain.MiaoshaUser;
import com.xiaolong.miaosha.redis.MiaoshaUserkey;
import com.xiaolong.miaosha.redis.RedisService;
import com.xiaolong.miaosha.result.CodeMsg;
import com.xiaolong.miaosha.util.MD5Util;
import com.xiaolong.miaosha.util.UUIDUtil;
import com.xiaolong.miaosha.vo.LoginVo;
import com.xiaolong.miaosha.exception.GlobalException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoshaUser getById(long id) {
        return miaoshaUserDao.getById(id);
    }

    public MiaoshaUser getByToken(String token, HttpServletResponse response) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser user= redisService.get(MiaoshaUserkey.token, token, MiaoshaUser.class);
        if (user != null) {
            addCookie(user, token, response);
        }
        return user;
    }

    public boolean login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw  new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calPass.equals(dbPass)) {
            throw  new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token = UUIDUtil.uuid();
        addCookie(user, token, response);

        return true;
    }

    private void addCookie(MiaoshaUser user,String token, HttpServletResponse response) {
        redisService.set(MiaoshaUserkey.token, token, user );
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserkey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }


}
