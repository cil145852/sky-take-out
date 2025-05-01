package com.sky.service;

import com.sky.dto.*;
import com.sky.enumeration.RoleType;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-28-19:13
 * @Description
 */

public interface OrderService {

    /**
     * 提交并生成订单信息
     *
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 历史订单分页查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult listPageOrders(OrdersPageQueryDTO ordersPageQueryDTO, RoleType roleType);

    /**
     * 根据订单id查询订单详细信息
     *
     * @param id
     * @return
     */
    OrderVO getOrderDetailByOrderId(Long id, RoleType roleType);

    /**
     * 取消订单,本质上是修改订单状态为6已取消
     *
     * @param ordersCancelDTO
     * @param roleType        角色类型 分为 用户和商家
     */
    void cancelOrder(OrdersCancelDTO ordersCancelDTO, RoleType roleType);

    /**
     * 再来一单,本质上是将订单明细再次添加到购物车
     *
     * @param id
     */
    void repeatOrder(Long id);

    /**
     * 统计订单信息,即各个状态的订单数量统计
     *
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 商家接单,本质上是修改订单状态为3已接单
     *
     * @param id
     */
    void confirm(Long id);

    /**
     * 商家拒单,本质上是修改订单状态为6已取消
     * 如果用户已经付款，则需要退款
     *
     * @param ordersRejectionDTO
     */
    void rejectOrder(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 商家派送订单,本质上是修改订单状态为4派送中
     *
     * @param id
     */
    void deliverOrder(Long id);

    /**
     * 完成订单,本质上是修改订单状态为5已完成
     *
     * @param id
     */
    void completeOrder(Long id);
}
