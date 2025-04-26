package com.sky.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
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
 * @CreateDate 2025-04-22-20:51
 * @Description
 */
@Slf4j
@Service
public class SetmealServiceImpl implements SetmealService {
    @Resource
    private SetmealMapper setmealMapper;

    @Resource
    private SetmealDishMapper setmealDishMapper;

    @Resource
    private DishMapper dishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        //先插入套餐获取套餐id
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);

        //再插入套餐和菜品关系
        Long setmealId = setmeal.getId();
        setmealDTO.getSetmealDishes().forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
        setmealDishMapper.insertBatch(setmealDTO.getSetmealDishes());
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealPageQueryDTO, setmeal);
        PageInfo<SetmealVO> pageInfo = new PageInfo<>(setmealMapper.selectWithCategory(setmeal));
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 批量删除套餐，如果有套餐正在售卖，则不能删除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        List<Setmeal> setmeals = setmealMapper.selectByIds(ids);
        if (setmeals.stream().anyMatch(setmeal -> setmeal.getStatus().equals(StatusConstant.ENABLE))) {
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        setmealMapper.deleteBatch(ids);
        setmealDishMapper.deleteBySetmealId(ids);
    }


    /**
     * 修改套餐信息
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        //先修改套餐表中的数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //由于套餐中修改的菜品信息可能包含未启售的菜品，所有约定修改套餐之后，套餐的状态为停售
        setmeal.setStatus(StatusConstant.DISABLE);
        setmealMapper.update(setmeal);

        //由于套餐与菜品是多对多的关系，即一个套餐在套餐菜品关联表中有多个数据，所以需要先删除套餐和菜品的关联数据，再重新插入
        setmealDishMapper.deleteBySetmealId(Collections.singletonList(setmealDTO.getId()));

        //插入套餐和菜品关系，插入前需要设置套餐id
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        Long setmealId = setmealDTO.getId();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 根据id查询套餐及套餐分类和套餐的菜品信息
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(Long id) {
        List<SetmealVO> setmealVOList = setmealMapper.selectWithCategoryAndDish(Setmeal.builder().id(id).build());
        if (!ObjectUtils.isEmpty(setmealVOList)) {
            return setmealVOList.get(0);
        } else {
            return null;
        }
    }

    /**
     * 启售停售套餐
     * 如果套餐中有菜品是停售的，则不能启售套餐
     * @param status
     * @param id
     */
    @Override
    public void startOrStopSale(Integer status, Long id) {
        if (status.equals(StatusConstant.ENABLE)) {
            //只有启售套餐操作时，查询套餐中的菜品是否都为启售状态
            List<Long> dishIds = setmealDishMapper.selectDishIdsBySetmealId(Collections.singletonList(id));
            List<Dish> dishList = dishMapper.selectByIds(dishIds);
            if (dishList.stream().anyMatch(dish -> dish.getStatus().equals(StatusConstant.DISABLE))) {
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }
        //启售套餐本质上就是修改套餐状态
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    /**
     * 根据分类id查询正在出售的套餐
     * @param categoryId
     * @return
     */
    @Override
    public List<Setmeal> listByCategoryId(Long categoryId) {
        Setmeal setmeal = Setmeal.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return setmealMapper.selectList(setmeal);
    }

    /**
     * 查询套餐中所包含的菜品项
     * @param setmealId
     * @return
     */
    @Override
    public List<DishItemVO> listDishItemBySetmealId(Long setmealId) {
        return setmealDishMapper.selectDishItemsBySetmealId(setmealId);
    }
}
