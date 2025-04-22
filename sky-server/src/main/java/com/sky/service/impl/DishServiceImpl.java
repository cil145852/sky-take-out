package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-21-22:26
 * @Description
 */
@Service
public class DishServiceImpl implements DishService {
    @Resource
    private DishMapper dishMapper;

    @Resource
    private DishFlavorMapper dishFlavorMapper;

    @Resource
    private SetmealDishMapper setmealDishMapper;


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
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        List<DishVO> dishVOList = dishMapper.selectWithCategory(Dish.builder()
                .name(dishPageQueryDTO.getName())
                .categoryId(dishPageQueryDTO.getCategoryId())
                .status(dishPageQueryDTO.getStatus()).build());
        PageInfo<DishVO> pageInfo = new PageInfo<>(dishVOList);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 批量删除菜品
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
        if(!ObjectUtils.isEmpty(setmealIds)) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品的同时删除菜品对应的口味数据
        dishMapper.deleteBatch(ids);
        dishFlavorMapper.deleteByDishIds(ids);

    }
}
