package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-25-20:50
 * @Description
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Resource
    private WeChatProperties weChatProperties;

    @Resource
    private UserMapper userMapper;

    /**
     * 通过微信授权码获取用户信息
     * 如果授权码不合法则抛出异常
     * 如果用户不存在，则自动创建用户，返回用户信息，如果用户存在，则直接返回用户信息
     * @param code
     * @return
     */
    @Override
    public User wxLogin(String code) {
        String openId = getOpenId(code);

        if (openId == null) {
            //授权码不合法抛出异常
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 判断用户是否存在
        User user = userMapper.getByOpenId(openId);
        if (user != null) {
            return user;
        }

        // 用户不存在，自动注册用户
        user = User.builder()
                .openid(openId)
                .createTime(LocalDateTime.now())
                .build();
        userMapper.insert(user);

        log.info("微信授权登录成功，用户信息为：{}", user);
        return user;
    }

    /**
     * 调用微信接口，通过微信授权码获取openid
     * @param code
     * @return
     */
    private String getOpenId(String code) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", weChatProperties.getAppid());
        paramMap.put("secret", weChatProperties.getSecret());
        paramMap.put("js_code", code);
        paramMap.put("grant_type", "authorization_code");
        JSONObject jsonObject = JSON.parseObject(HttpClientUtil.doGet(WX_LOGIN_URL, paramMap));
        return jsonObject.getString("openid");
    }
}
