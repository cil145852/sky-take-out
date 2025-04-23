package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-22-20:48
 * @Description 套餐业务接口
 */
@Service
public interface SetmealService {

   /**
    * 新增套餐
    * @param setmealDTO
    */
   void save(SetmealDTO setmealDTO);

   /**
    * 分页查询套餐数据
    * @param setmealPageQueryDTO
    * @return
    */
   PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

   /**
    * 批量删除套餐
    * @param ids
    */
   void deleteBatch(List<Long> ids);
}
