package com.sky.controller.admin;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.enumeration.RoleType;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-30-10:26
 * @Description
 */
@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "管理端订单接口")
@Slf4j
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 根据订单条件进行分页查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("管理端订单搜索")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("管理端订单搜索: {}", ordersPageQueryDTO);
        PageResult pageResult = orderService.listPageOrders(ordersPageQueryDTO, RoleType.EMPLOYEE);
        return Result.success(pageResult);
    }

    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> statistics() {
        log.info("各个状态的订单数量统计");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> getOrderById(@PathVariable("id") Long id) {
        log.info("查询订单详情: {}", id);
        OrderVO orderVO = orderService.getOrderDetailByOrderId(id, RoleType.EMPLOYEE);
        return Result.success(orderVO);
    }
}
