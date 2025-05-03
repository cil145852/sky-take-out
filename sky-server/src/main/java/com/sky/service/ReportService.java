package com.sky.service;

import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

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
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO queryTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 统计指定时间范围内的用户数据
     * @param begin
     * @param end
     * @return
     */
    UserReportVO queryUserStatistics(LocalDate begin, LocalDate end);
}
