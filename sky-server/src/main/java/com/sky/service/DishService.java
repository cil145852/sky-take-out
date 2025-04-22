package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

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

   /**
    * 根据id查询菜品和菜品对应的分类和口味数据
    * @param dish
    * @return
    */
   DishVO getWithCategoryAndFlavorsById(Long dish);

   /**
    * 修改菜品和口味数据
    * @param dishDTO
    */
   void update(DishDTO dishDTO);

   /**
    * 启售停售菜品本质上是修改菜品状态
    * @param status
    * @param id
    */
   void startOrStopSale(Integer status, Long id);

   /**
    * 根据分类id查询菜品
    * @param categoryId
    * @return
    */
   List<Dish> getByCategoryId(Long categoryId);
}
