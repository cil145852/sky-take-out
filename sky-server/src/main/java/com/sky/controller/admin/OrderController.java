package com.sky.controller.admin;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.enumeration.RoleType;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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

   @GetMapping("/conditionSearch")
   @ApiOperation("管理端订单搜索")
   public Result<PageResult> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
      PageResult pageResult = orderService.listPageOrders(ordersPageQueryDTO, RoleType.EMPLOYEE);
      return Result.success(pageResult);
   }
}
