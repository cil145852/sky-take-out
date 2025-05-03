package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

   @Override
   public TurnoverReportVO queryTurnoverStatistics(LocalDate begin, LocalDate end) {
      List<LocalDate> dateList = new ArrayList<>();
      do {
         dateList.add(begin);
         begin = begin.plusDays(1);
      } while (!begin.isAfter(end));

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
}
