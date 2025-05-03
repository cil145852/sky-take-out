package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> queryTurnoverStatistics(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        log.info("营业额统计");
        return Result.success(reportService.queryTurnoverStatistics(begin, end));
    }

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> queryUserStatistics(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        log.info("用户统计");
        return Result.success(reportService.queryUserStatistics(begin, end));
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> queryOrdersStatistics(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        log.info("订单统计");
        return Result.success(reportService.queryOrdersStatistics(begin, end));
    }

    /**
     * 查询销量排名top10
     *
     * @param begin
     * @param end
     * @return
     */
    @GetMapping("/top10")
    @ApiOperation("查询销量排名top10")
    public Result<SalesTop10ReportVO> getSalesTop10(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        log.info("查询销量排名top10");
        return Result.success(reportService.getSalesTop10(begin, end));
    }

    /**
     * 导出Excel报表
     *
     * @return
     */
    @GetMapping("/export")
    @ApiOperation("导出Excel报表")
    public void export(HttpServletResponse response) {
        try {
            //response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            //response.setHeader("Content-Disposition", "attachment; filename=\"report.xlsx\"; filename*=UTF-8''"
            //        + URLEncoder.encode("report.xlsx", "UTF-8").replaceAll("\\+", "%20"));
            reportService.exportBusinessData(response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
