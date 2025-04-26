package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-21-21:59
 * @Description 菜品管理
 */
@RestController("adminDishController")
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
   @Resource
   private DishService dishService;

   /**
    * 新增菜品
    * @param dishDTO
    * @return
    */
   @PostMapping
   @ApiOperation("新增菜品")
   public Result save(@RequestBody DishDTO dishDTO) {
      log.info("新增菜品：{}", dishDTO);
      dishService.saveWithFlavor(dishDTO);
      return Result.success();
   }

   /**
    * 菜品分页查询
    * @param dishPageQueryDTO
    * @return
    */
   @GetMapping("/page")
   @ApiOperation("菜品分页查询")
   public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
      log.info("菜品分页查询：{}", dishPageQueryDTO);
      PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
      return Result.success(pageResult);
   }

   /**
    * 批量删除菜品
    * @param ids
    * @return
    */
   @DeleteMapping
   @ApiOperation("批量删除菜品")
   public Result delete(@RequestParam List<Long> ids) {
      log.info("批量删除菜品：{}", ids);
      dishService.deleteBatch(ids);
      return Result.success();
   }

   /**
    * 根据id查询菜品
    * @param id
    * @return
    */
   @GetMapping("/{id}")
   @ApiOperation("根据id查询菜品")
   public Result<DishVO> getById(@PathVariable("id") Long id) {
      DishVO dishVO = dishService.getWithCategoryAndFlavorsById(id);
      log.info("根据id查询菜品：{}", dishVO);
      return Result.success(dishVO);
   }

   /**
    * 修改菜品信息
    * @param dishDTO
    * @return
    */
   @PutMapping
   @ApiOperation("修改菜品")
   public Result update(@RequestBody DishDTO dishDTO) {
      log.info("修改菜品信息：{}", dishDTO);
      dishService.update(dishDTO);
      return Result.success();
   }

   /**
    * 启售或停售商品
    * @param status
    * @param id
    * @return
    */
   @PostMapping("/status/{status}")
   @ApiOperation("启售或停售商品")
   public Result startOrStopSale(@PathVariable Integer status, @RequestParam Long id) {
      dishService.startOrStopSale(status, id);
      return Result.success();
   }

   /**
    * 根据分类id查询菜品
    * @param categoryId
    * @return
    */
   @GetMapping("/list")
   @ApiOperation("根据分类id查询菜品")
   public Result<List<Dish>> getByCategoryId(@RequestParam("categoryId") Long categoryId) {
      log.info("根据分类id查询菜品：{}", categoryId);
      return Result.success(dishService.getByCategoryId(categoryId));
   }
}
