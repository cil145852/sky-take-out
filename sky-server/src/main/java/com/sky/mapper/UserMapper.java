package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-25-21:08
 * @Description
 */
@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     *
     * @param openid
     * @return
     */
    User getByOpenId(String openid);

    /**
     * 新增用户
     *
     * @param user
     */
    void insert(User user);

    /**
     * 条件查询用户数据
     *
     * @param user
     * @return
     */
    List<User> selectList(User user);

    /**
     * 根据时间区间统计用户数量
     *
     * @param beginTime
     * @param endTime
     * @return
     */
    Integer countUserByDateTime(
            @Param("beginTime") LocalDateTime beginTime,
            @Param("endTime") LocalDateTime endTime
    );
}
