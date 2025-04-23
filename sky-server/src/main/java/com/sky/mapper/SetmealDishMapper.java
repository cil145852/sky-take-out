package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-22-11:08
 * @Description
 */
@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询套餐id
     * @param dishIds
     * @return
     */
    List<Long> selectSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 批量插入套餐菜品关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除与套餐有关的套餐菜品关联数据
     * @param setmealIds
     */
    void deleteBySetmealId(List<Long> setmealIds);

    /**
     * 根据套餐id查询菜品id
     * @param setmealId
     * @return
     */
    List<Long> selectDishIdsBySetmealId(List<Long> setmealIds);
}
