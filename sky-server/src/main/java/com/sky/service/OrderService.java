package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
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
}
