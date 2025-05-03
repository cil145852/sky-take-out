package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-28-20:10
 * @Description
 */
@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单明细数据
     * @param orderDetails
     */
    void insertBatch(List<OrderDetail> orderDetails);

    /**
     * 根据订单id查询订单明细
     * @param orderId
     * @return
     */
    List<OrderDetail> selectListByOrderId(Long orderId);

    List<GoodsSalesDTO> getSalesTop10(
            @Param("begin") LocalDateTime begin,
            @Param("end") LocalDateTime end
    );
}
