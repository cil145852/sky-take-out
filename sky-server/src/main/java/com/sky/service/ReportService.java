package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.io.OutputStream;
import java.time.LocalDate;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-05-03-14:22
 * @Description
 */

public interface ReportService {

    /**
     * 统计指定时间范围内的营业额
     *
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO queryTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间范围内的用户数据
     *
     * @param begin
     * @param end
     * @return
     */
    UserReportVO queryUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间范围内的订单数据
     *
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO queryOrdersStatistics(LocalDate begin, LocalDate end);

    /**
     * 查询指定时间范围菜品/套餐销量排名top10
     *
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);

    /**
     * 导出Excel运营数据报表
     *
     * @param outputStream 输出流，用于将Excel数据写入到输出流中
     */
    void exportBusinessData(OutputStream outputStream);
}
