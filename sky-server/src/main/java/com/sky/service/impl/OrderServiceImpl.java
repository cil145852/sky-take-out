package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.enumeration.RoleType;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-28-19:14
 * @Description
 */

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private AddressBookMapper addressBookMapper;

    @Resource
    private ShoppingCartMapper shoppingCartMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    //@Resource
    //private WeChatPayUtil weChatPayUtil;

    /**
     * 提交并生成订单信息
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //校验订单
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectList(ShoppingCart.builder().userId(userId).build());
        if (ObjectUtils.isEmpty(shoppingCartList)) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //生成订单信息，插入到数据库中
        Orders orders = createOrders(ordersSubmitDTO);
        orderMapper.insert(orders);
        Long orderId = orders.getId();

        //向订单明细表插入n条数据
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map(shoppingCart -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orderId);
            return orderDetail;
        }).collect(Collectors.toList());
        orderDetailMapper.insertBatch(orderDetailList);

        //清空购物车数据
        shoppingCartMapper.deleteByUserId(userId);
        return OrderSubmitVO.builder()
                .id(orderId)
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();

    }

    /**
     * 根据订单信息生成订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    private Orders createOrders(OrdersSubmitDTO ordersSubmitDTO) {
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.selectList(User.builder().id(userId).build()).get(0);
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setNumber(UUID.randomUUID().toString().replaceAll("-", ""));
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setUserName(user.getName());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        return orders;
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.selectList(User.builder().id(userId).build()).get(0);

        //调用微信支付接口，生成预支付交易单
        //JSONObject jsonObject = weChatPayUtil.pay(
        //        ordersPaymentDTO.getOrderNumber(), //商户订单号
        //        new BigDecimal(0.01), //支付金额，单位 元
        //        "苍穹外卖订单", //商品描述
        //        user.getOpenid() //微信用户的openid
        //);
        //
        //if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
        //    throw new OrderBusinessException("该订单已支付");
        //}
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        //vo.setPackageStr(jsonObject.getString("package"));
        paySuccess(ordersPaymentDTO.getOrderNumber());
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    @Override
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 历史订单分页查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult listPageOrders(OrdersPageQueryDTO ordersPageQueryDTO, RoleType roleType) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Orders queryOrders = new Orders();
        BeanUtils.copyProperties(ordersPageQueryDTO, queryOrders);

        if (roleType == RoleType.USER) {
            queryOrders.setUserId(BaseContext.getCurrentId());
        }

        //由于是分页查询，所以不能联表查询，只能先获取订单列表，再根据订单id获取订单详情
        PageInfo<Orders> pageInfo = new PageInfo<>(orderMapper.selectList(queryOrders, ordersPageQueryDTO.getBeginTime(), ordersPageQueryDTO.getEndTime()));

        List<OrderVO> orderVOList = getOrderVO(pageInfo.getList());

        return new PageResult(pageInfo.getTotal(), orderVOList);
    }

    /**
     * 获取订单详细信息
     *
     * @param ordersList
     * @return
     */
    private List<OrderVO> getOrderVO(List<Orders> ordersList) {
        return ordersList.stream().map(orders -> {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);
            List<OrderDetail> orderDetailList = orderDetailMapper.selectListByOrderId(orders.getId());
            orderVO.setOrderDetailList(orderDetailList);
            orderVO.setOrderDishes(getOrderDishStr(orderDetailList));
            return orderVO;
        }).collect(Collectors.toList());
    }

    /**
     * 获取订单菜品信息
     *
     * @param orderDetailList
     * @return
     */
    private String getOrderDishStr(List<OrderDetail> orderDetailList) {
        StringBuilder sb = new StringBuilder(50);
        orderDetailList.forEach(orderDetail -> {
            sb.append(orderDetail.getName()).append("*").append(orderDetail.getNumber()).append(";");
        });
        return sb.toString();
    }

    /**
     * 根据订单id查询订单详细信息
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO getOrderDetailByOrderId(Long id, RoleType roleType) {
        Orders orders = Orders.builder()
                .id(id)
                .build();
        if (roleType == RoleType.USER) {
            orders.setUserId(BaseContext.getCurrentId());
        }
        OrderVO orderVO = orderMapper.selectListWithOrderDetails(orders).get(0);
        orderVO.setOrderDishes(getOrderDishStr(orderDetailMapper.selectListByOrderId(orderVO.getId())));
        return orderVO;
    }

    /**
     * 取消订单,本质上是修改订单状态为6已取消
     *
     * @param ordersCancelDTO
     * @param roleType        角色类型 分为 用户和商家
     */
    @Override
    public void cancelOrder(OrdersCancelDTO ordersCancelDTO, RoleType roleType) {
        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        if (roleType == RoleType.USER) {
            orders.setUserId(BaseContext.getCurrentId());
        }
        List<Orders> ordersList = orderMapper.selectList(orders, null, null);
        if (ObjectUtils.isEmpty(ordersList)) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        orders = ordersList.get(0);
        //对于用户,如果商家已接单或订单已完成/已取消，则不能取消
        if (roleType == RoleType.USER && orders.getStatus() > Orders.TO_BE_CONFIRMED) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        //对于商家，订单已经完成或取消不能取消，待接单时也不能取消而是是拒绝接单
        if (roleType == RoleType.EMPLOYEE
                && (orders.getStatus() > Orders.DELIVERY_IN_PROGRESS
                || orders.getStatus().equals(Orders.TO_BE_CONFIRMED))) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //如果用户已经付款，则需要退款
        if (orders.getStatus() > Orders.PENDING_PAYMENT && Objects.equals(orders.getPayStatus(), Orders.PAID)) {
            refund(orders);
        }

        //订单状态修改为 已取消
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orderMapper.update(orders);
    }

    /**
     * 再来一单,本质上是将订单明细再次添加到购物车
     *
     * @param id
     */
    @Override
    public void repeatOrder(Long id) {
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> shoppingCartList = orderDetailMapper.selectListByOrderId(id)
                .stream()
                .map(orderDetail -> {
                    ShoppingCart shoppingCart = ShoppingCart.builder()
                            .userId(userId)
                            .createTime(LocalDateTime.now())
                            .build();
                    BeanUtils.copyProperties(orderDetail, shoppingCart);
                    return shoppingCart;
                })
                .collect(Collectors.toList());
        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    /**
     * 统计订单信息,即各个状态的订单数量统计
     *
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        Orders orders = new Orders();
        //待接单数量
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        Integer toBeConfirmedCount = orderMapper.selectCount(orders);

        //已接单待派送数量
        orders.setStatus(Orders.CONFIRMED);
        Integer confirmedCount = orderMapper.selectCount(orders);

        //派送中数量
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        Integer deliveryInProgressCount = orderMapper.selectCount(orders);
        //返回数据
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmedCount);
        orderStatisticsVO.setConfirmed(confirmedCount);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgressCount);
        return orderStatisticsVO;
    }

    /**
     * 商家接单,本质上是修改订单状态为3已接单
     *
     * @param id
     */
    @Override
    public void confirm(Long id) {
        Orders orders = Orders.builder()
                .id(id)
                .status(Orders.CONFIRMED)
                .build();
        orderMapper.update(orders);
    }

    /**
     * 商家拒单,本质上是修改订单状态为6已取消
     * 如果用户已经付款，则需要退款
     *
     * @param ordersRejectionDTO
     */
    @Override
    public void rejectOrder(OrdersRejectionDTO ordersRejectionDTO) {
        Orders orders = orderMapper.selectList(Orders.builder().id(ordersRejectionDTO.getId()).build(), null, null).get(0);
        //如果订单状态不是待接单，则不能拒单
        if (orders == null || orders.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        if (orders.getPayStatus().equals(Orders.PAID)) {
            refund(orders);
        }
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 退款某个订单
     *
     * @param orders
     */
    private void refund(Orders orders) {
        log.info("正在给订单号 {} 退款", orders.getNumber());
        //调用微信支付退款接口
        //weChatPayUtil.refund(
        //        orders.getNumber(), //商户订单号
        //        orders.getNumber(), //商户退款单号
        //        new BigDecimal(0.01),//退款金额，单位 元
        //        new BigDecimal(0.01));//原订单金额

        //支付状态修改为 退款
        orders.setPayStatus(Orders.REFUND);
    }
}
