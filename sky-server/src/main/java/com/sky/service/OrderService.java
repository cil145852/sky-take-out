package com.sky.service;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-28-19:13
 * @Description
 */

public interface OrderService {

   /**
    * 提交并生成订单信息
    * @param ordersSubmitDTO
    * @return
    */
   OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

   /**
    * 订单支付
    * @param ordersPaymentDTO
    * @return
    */
   OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

   /**
    * 支付成功，修改订单状态
    * @param outTradeNo
    */
   void paySuccess(String outTradeNo);

   /**
    * 历史订单分页查询
    * @param ordersPageQueryDTO
    * @return
    */
   PageResult listPageOrders(OrdersPageQueryDTO ordersPageQueryDTO);
}
