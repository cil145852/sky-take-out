package com.sky.service;

import com.sky.dto.DishDTO;

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
}
