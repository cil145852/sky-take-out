package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.entity.Orders;
import com.sky.entity.Setmeal;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private DishMapper dishMapper;
    @Resource
    private SetmealMapper setmealMapper;

    /**
     * 根据时间段统计营业数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {

         //营业额：当日已完成订单的总金额
         //有效订单：当日已完成订单的数量
         //订单完成率：有效订单数 / 总订单数
         //平均客单价：营业额 / 有效订单数
         //新增用户：当日新增用户的数量

        //查询总订单数
        Integer totalOrderCount = orderMapper.countByDateTime(begin, end, null);

        //营业额
        Double turnover = orderMapper.sumOfAmountByDateTime(begin, end, Orders.COMPLETED);
        turnover = turnover == null? 0.0 : turnover;

        //有效订单数
        Integer validOrderCount = orderMapper.countByDateTime(begin, end, Orders.COMPLETED);

        Double unitPrice = 0.0;

        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0 && validOrderCount != 0){
            //订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            //平均客单价
            unitPrice = turnover / validOrderCount;
        }

        //新增用户数
        Integer newUsers = userMapper.countUserByDateTime(begin, end);

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }


    /**
     * 查询订单管理数据
     *
     * @return
     */
    @Override
    public OrderOverViewVO getOrderOverView() {

        LocalDateTime begin = LocalDateTime.now().with(LocalTime.MIN);

        //待接单
        Integer waitingOrders = orderMapper.countByDateTime(begin, null, Orders.TO_BE_CONFIRMED);

        //待派送
        Integer deliveredOrders = orderMapper.countByDateTime(begin, null, Orders.CONFIRMED);

        //已完成
        Integer completedOrders = orderMapper.countByDateTime(begin, null, Orders.COMPLETED);

        //已取消
        Integer cancelledOrders = orderMapper.countByDateTime(begin, null, Orders.CANCELLED);

        //全部订单
        Integer allOrders = orderMapper.countByDateTime(begin, null, null);

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 查询菜品总览
     *
     * @return
     */
    @Override
    public DishOverViewVO getDishOverView() {
        Dish dish = new Dish();
        dish.setStatus(StatusConstant.ENABLE);
        Integer sold = dishMapper.countDish(dish);

        dish.setStatus(StatusConstant.DISABLE);
        Integer discontinued = dishMapper.countDish(dish);

        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询套餐总览
     *
     * @return
     */
    @Override
    public SetmealOverViewVO getSetmealOverView() {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(StatusConstant.ENABLE);
        Integer sold = setmealMapper.countSetmeal(setmeal);

        setmeal.setStatus(StatusConstant.DISABLE);
        Integer discontinued = setmealMapper.countSetmeal(setmeal);

        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }
}
