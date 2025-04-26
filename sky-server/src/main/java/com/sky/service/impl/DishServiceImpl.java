package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-21-22:26
 * @Description
 */
@Slf4j
@Service
public class DishServiceImpl implements DishService {
    @Resource
    private DishMapper dishMapper;

    @Resource
    private DishFlavorMapper dishFlavorMapper;

    @Resource
    private SetmealDishMapper setmealDishMapper;

    @Resource
    private SetmealMapper setmealMapper;


    /**
     * 新增菜品同时添加菜品对应的口味数据
     *
     * @param dishDTO
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        //插入一条菜品数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        Long dishId = dish.getId();
        //插入n条口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (!ObjectUtils.isEmpty(flavors)) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 分页查询菜品和分类名称
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishPageQueryDTO, dish);
        List<DishVO> dishVOList = dishMapper.selectWithCategory(dish);
        PageInfo<DishVO> pageInfo = new PageInfo<>(dishVOList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 批量删除菜品
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //获取要删除菜品的状态信息如果状态为起售状态就抛出业务异常，不能删除
        List<Dish> dishList = dishMapper.selectByIds(ids);
        if (dishList.stream().anyMatch(dish -> dish.getStatus().equals(StatusConstant.ENABLE))) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }

        //如果菜品在某个套餐中，不能删除
        List<Long> setmealIds = setmealDishMapper.selectSetmealIdsByDishIds(ids);
        if (!ObjectUtils.isEmpty(setmealIds)) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品的同时删除菜品对应的口味数据
        dishMapper.deleteBatch(ids);
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 根据id查询菜品和菜品对应的分类和口味数据
     *
     * @param id
     * @return
     */
    @Override
    public DishVO getWithCategoryAndFlavorsById(Long id) {
        //通过id进行联表查询获取DishVO
        List<DishVO> dishVOList = dishMapper.selectWithCategoryAndFlavors(Dish.builder().id(id).build());
        if (!ObjectUtils.isEmpty(dishVOList)) {
            return dishVOList.get(0);
        } else {
            return null;
        }
    }

    /**
     * 修改菜品和口味数据
     *
     * @param dishDTO
     */
    @Override
    @Transactional
    public void update(DishDTO dishDTO) {
        //修改菜品数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        //由于菜品和口味是一对多的关系，所以需要先删除菜品对应的口味数据，再插入新的口味数据
        dishFlavorMapper.deleteByDishIds(Collections.singletonList(dishDTO.getId()));
        if (!ObjectUtils.isEmpty(dishDTO.getFlavors())) {
            dishDTO.getFlavors().forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(dishDTO.getFlavors());
        }
    }

    /**
     * 启售停售菜品，本质上是修改菜品状态
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStopSale(Integer status, Long id) {
        dishMapper.update(Dish.builder().id(id).status(status).build());

        //如果菜品状态为停售，则将菜品所在的套餐状态也修改为停售
        if (status.equals(StatusConstant.DISABLE)) {
            List<Long> setmealIds = setmealDishMapper.selectSetmealIdsByDishIds(Collections.singletonList(id));
            for (Long setmealId : setmealIds) {
                setmealMapper.update(Setmeal.builder().id(setmealId).status(StatusConstant.DISABLE).build());
            }
        }
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId 分类id
     * @return 查询到的菜品
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        return dishMapper.selectList(Dish.builder().categoryId(categoryId).build());
    }

    /**
     * 根据分类id查询正在出售菜品及其口味数据
     * @param categoryId
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.selectWithCategoryAndFlavors(dish);
    }
}
