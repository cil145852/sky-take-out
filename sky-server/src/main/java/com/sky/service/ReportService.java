package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-05-03-14:22
 * @Description
 */

public interface ReportService {
    TurnoverReportVO queryTurnoverStatistics(LocalDate begin, LocalDate end);
}
