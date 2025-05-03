package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liang
 */
@Mapper
public interface SetmealMapper {

    /**
     * 根据条件查询套餐数量
     *
     * @param setmeal
     * @return
     */
    Integer countSetmeal(Setmeal setmeal);

    /**
     * 修改菜品套餐
     *
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 新增套餐
     *
     * @param setmeal
     */
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
     * 根据查询条件查询套餐信息和套餐所属分类的信息
     *
     * @param setmeal
     * @return
     */
    List<SetmealVO> selectWithCategory(Setmeal setmeal);

    /**
     * 根据id批量删除套餐
     *
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id批量查询套餐
     *
     * @param ids
     * @return
     */
    List<Setmeal> selectByIds(List<Long> ids);

    /**
     * 根据条件查询套餐及套餐分类和关联的菜品信息
     *
     * @param setmeal
     * @return
     */
    List<SetmealVO> selectWithCategoryAndDish(Setmeal setmeal);

    /**
     * 根据条件查询套餐
     *
     * @param setmeal
     * @return
     */
    List<Setmeal> selectList(Setmeal setmeal);
}
