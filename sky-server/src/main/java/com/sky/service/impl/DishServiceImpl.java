package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
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
}
