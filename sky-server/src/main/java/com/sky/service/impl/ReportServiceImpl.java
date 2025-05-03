package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    @Resource
    private WorkspaceService workspaceService;

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


    /**
     * 导出Excel运营数据报表
     *
     * @param outputStream 输出流，用于将Excel数据写入到输出流中
     */
    @Override
    public void exportBusinessData(OutputStream outputStream) {
        //查询数据库，获取营业数据

        LocalDate today = LocalDate.now();
        LocalDate beginDate = today.minusDays(30);
        LocalDate endDate = today.minusDays(1);
        BusinessDataVO businessData = workspaceService.getBusinessData(
                LocalDateTime.of(beginDate, LocalTime.MIN),
                LocalDateTime.of(endDate, LocalTime.MAX)
        );

        //将数据写入Excel文件中
        try (
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
                XSSFWorkbook excel = new XSSFWorkbook(is)
        ){
            XSSFSheet sheet = excel.getSheet("Sheet1");
            //填写概览数据
            //填充第2行数据
            sheet.getRow(1).getCell(1).setCellValue("时间:" + beginDate + "至" + endDate);
            //填充第4行数据
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            //填充第5行数据
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

            //填写明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = beginDate.plusDays(i);
                BusinessDataVO businessDataVO = workspaceService.getBusinessData(
                        LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(String.valueOf(date));
                row.getCell(2).setCellValue(businessDataVO.getTurnover());
                row.getCell(3).setCellValue(businessDataVO.getValidOrderCount());
                row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessDataVO.getUnitPrice());
                row.getCell(6).setCellValue(businessDataVO.getNewUsers());
            }
            //将excel数据写入到输出流中
            excel.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
