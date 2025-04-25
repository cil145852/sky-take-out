package com.sky.service;

import com.sky.entity.User;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-25-20:42
 * @Description
 */
public interface UserService {
    /**
     * 微信登录
     * @param code
     * @return
     */
    User wxLogin(String code);
}
