package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-05-03-14:22
 * @Description
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    /**
     * 统计指定时间范围内的营业额
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO queryTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Double> turnoverList = new ArrayList<>();
        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Double turnover = orderMapper.sumOfAmountByDateTime(beginTime, endTime, Orders.COMPLETED);
            turnoverList.add(turnover == null ? 0.0 : turnover);
        });
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.collectionToCommaDelimitedString(dateList))
                .turnoverList(StringUtils.collectionToCommaDelimitedString(turnoverList))
                .build();
    }

    /**
     * 统计指定时间范围内的用户数据
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO queryUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer newUserCount = userMapper.countUserByDateTime(beginTime, endTime);
            Integer totalUserCount = userMapper.countUserByDateTime(null, endTime);
            newUserList.add(newUserCount);
            totalUserList.add(totalUserCount);
        });
        return UserReportVO
                .builder()
                .dateList(StringUtils.collectionToCommaDelimitedString(dateList))
                .newUserList(StringUtils.collectionToCommaDelimitedString(newUserList))
                .totalUserList(StringUtils.collectionToCommaDelimitedString(totalUserList))
                .build();
    }

    /**
     * 统计指定时间范围内的订单数据
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO queryOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        dateList.forEach(date -> {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //查询当日的订单数量
            Integer orderCount = orderMapper.countByDateTime(beginTime, endTime, null);
            orderCountList.add(orderCount);
            //查询当日的有效订单数量
            Integer validOrderCount = orderMapper.countByDateTime(beginTime, endTime, Orders.COMPLETED);
            validOrderCountList.add(validOrderCount);
        });
        //订单总数
        Integer orderCount = sum(orderCountList);
        //有效订单总数
        Integer validOrderCount = sum(validOrderCountList);
        //订单完成率
        Double orderCompletionRate = 0.0;
        if (orderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / orderCount;
        }
        return OrderReportVO
                .builder()
                .dateList(StringUtils.collectionToCommaDelimitedString(dateList))
                .orderCountList(StringUtils.collectionToCommaDelimitedString(orderCountList))
                .validOrderCountList(StringUtils.collectionToCommaDelimitedString(validOrderCountList))
                .totalOrderCount(orderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 查询指定时间范围菜品/套餐销量排名top10
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10 = orderDetailMapper.getSalesTop10(beginTime, endTime);

        List<String> nameList = salesTop10
                .stream()
                .map(GoodsSalesDTO::getName)
                .collect(Collectors.toList());

        List<Integer> numberList = salesTop10
                .stream()
                .map(GoodsSalesDTO::getNumber)
                .collect(Collectors.toList());

        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.collectionToCommaDelimitedString(nameList))
                .numberList(StringUtils.collectionToCommaDelimitedString(numberList))
                .build();
    }

    /**
     * 获取指定时间范围内的日期集合
     *
     * @param begin
     * @param end
     * @return
     */
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>(32);
        do {
            dateList.add(begin);
            begin = begin.plusDays(1);
        } while (!begin.isAfter(end));
        return dateList;
    }

    /**
     * 求和
     *
     * @param list
     * @return
     */
    private Integer sum(List<Integer> list) {
        return list.stream().reduce(0, Integer::sum);
    }
}
