package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author liang
 */
@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 根据id批量查询菜品数据
     * @param ids
     * @return
     */
    List<Dish> selectByIds(List<Long> ids);

    /**
     * 根据id批量删除菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据条件查询菜品和菜品对应的分类和口味数据
     * @param dish
     * @return
     */
    List<DishVO> selectWithCategoryAndFlavors(Dish dish);

    /**
     * 根据条件查询菜品和菜品对应的分类
     * @param dish
     * @return
     */
    List<DishVO> selectWithCategory(Dish dish);

    /**
     * 修改菜品信息
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据条件查询菜品数据
     * @param dish
     * @return
     */
    List<Dish> selectList(Dish dish);
}
