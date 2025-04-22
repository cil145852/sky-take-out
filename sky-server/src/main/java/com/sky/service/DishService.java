package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

import java.util.List;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-21-22:25
 * @Description
 */

public interface DishService {

   /**
    * 新增菜品同时添加菜品对应的口味数据
    * @param dishDTO
    */
   void saveWithFlavor(DishDTO dishDTO);

   /**
    * 菜品分页查询和分类名称
    * @param dishPageQueryDTO
    * @return
    */
   PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

   /**
    * 批量删除菜品
    * @param ids
    */
   void deleteBatch(List<Long> ids);
}
