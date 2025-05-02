package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-05-02-11:08
 * @Description 订单定时处理器
 */
@Slf4j
@Component
public class OrderTask {
    @Resource
    private OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 0/2 * * * ?")
    public void processTimeoutOrder() {
        log.info("处理超时订单");
        Orders queryOrder = Orders.builder()
                .status(Orders.PENDING_PAYMENT)
                .build();
        List<Orders> ordersList = orderMapper.selectList(queryOrder, null, LocalDateTime.now().minusMinutes(15));
        log.info("查询到超时订单：{}", ordersList);
        if (!ObjectUtils.isEmpty(ordersList)) {
            ordersList.forEach(orders -> {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            });
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrders() {
        log.info("将派送的订单自动设置为已完成");
        Orders queryOrder = Orders.builder()
                .status(Orders.DELIVERY_IN_PROGRESS)
                .build();
        List<Orders> ordersList = orderMapper.selectList(queryOrder, null, LocalDateTime.now().minusHours(1));
        log.info("查询到派送中的订单：{}", ordersList);
        if (!ObjectUtils.isEmpty(ordersList)) {
            ordersList.forEach(orders -> {
                orders.setStatus(Orders.COMPLETED);
                orders.setDeliveryTime(LocalDateTime.now());
                orderMapper.update(orders);
            });

        }
    }

}
