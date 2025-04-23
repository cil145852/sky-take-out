package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-22-20:48
 * @Description 套餐业务接口
 */
@Service
public interface SetmealService {

   /**
    * 新增套餐
    * @param setmealDTO
    */
   void save(SetmealDTO setmealDTO);

   /**
    * 分页查询套餐数据
    * @param setmealPageQueryDTO
    * @return
    */
   PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

   /**
    * 批量删除套餐
    * @param ids
    */
   void deleteBatch(List<Long> ids);

   /**
    * 修改套餐
    * @param setmealDTO
    */
   void update(SetmealDTO setmealDTO);

   /**
    * 根据id查询套餐及套餐分类和套餐的菜品信息
    * @param id
    * @return
    */
   SetmealVO getById(Long id);

   /**
    * 启售停售套餐
    * @param status
    * @param id
    */
   void startOrStopSale(Integer status, Long id);
}
