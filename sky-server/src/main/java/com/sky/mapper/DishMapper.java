package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liang
 */
@Mapper
public interface DishMapper {

    /**
     * 新增菜品
     *
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 根据id批量查询菜品数据
     *
     * @param ids
     * @return
     */
    List<Dish> selectByIds(List<Long> ids);

    /**
     * 根据id批量删除菜品
     *
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据条件查询菜品和菜品对应的分类和口味数据
     *
     * @param dish
     * @return
     */
    List<DishVO> selectWithCategoryAndFlavors(Dish dish);

    /**
     * 根据条件查询菜品和菜品对应的分类
     *
     * @param dish
     * @return
     */
    List<DishVO> selectWithCategory(Dish dish);

    /**
     * 修改菜品信息
     *
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据条件查询菜品数据
     *
     * @param dish
     * @return
     */
    List<Dish> selectList(Dish dish);

    /**
     * 根据条件统计菜品数量
     *
     * @param dish
     * @return
     */
    Integer countDish(Dish dish);
}
