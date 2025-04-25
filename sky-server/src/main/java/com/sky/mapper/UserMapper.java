package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-25-21:08
 * @Description
 */
@Mapper
public interface UserMapper {
   User getByOpenId(String openid);

   void insert(User user);
}
