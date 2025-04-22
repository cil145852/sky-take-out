package com.sky.service;

import com.sky.dto.SetmealDTO;
import org.springframework.stereotype.Service;

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
}
