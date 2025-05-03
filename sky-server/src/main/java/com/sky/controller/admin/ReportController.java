package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-05-03-14:14
 * @Description
 */
@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据统计相关接口")
@Slf4j
public class ReportController {

    @Resource
    private ReportService reportService;

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    @RequestMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> queryTurnoverStatistics(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额统计");
        TurnoverReportVO turnoverReportVO = reportService.queryTurnoverStatistics(begin, end);
        return Result.success(turnoverReportVO);
    }

}
