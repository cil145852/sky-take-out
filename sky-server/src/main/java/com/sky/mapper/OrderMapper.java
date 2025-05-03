package com.sky.mapper;

import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-28-19:48
 * @Description
 */
@Mapper
public interface OrderMapper {

    /**
     * 插入订单数据
     *
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     *
     * @param orderNumber
     */
    @Select("SELECT * FROM orders WHERE number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     *
     * @param orders
     */
    void update(@Param("orders") Orders orders);

    /**
     * 条件查询订单
     *
     * @param orders
     * @return
     */
    List<Orders> selectList(@Param("orders") Orders orders,
                            @Param("beginTime") LocalDateTime beginTime,
                            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 条件查询订单详细信息
     *
     * @param orders
     * @return
     */
    List<OrderVO> selectListWithOrderDetails(@Param("orders") Orders orders);

    /**
     * 根据条件统计订单数量
     *
     * @param orders
     * @return
     */
    Integer selectCount(@Param("orders") Orders orders);

    /**
     * 根据时间范围统计订单总金额
     * @param beginTime
     * @param endTime
     * @param status
     * @return
     */
    Double sumOfAmountByDateTime(
            @Param("beginTime") LocalDateTime beginTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("status") Integer status
    );
}
