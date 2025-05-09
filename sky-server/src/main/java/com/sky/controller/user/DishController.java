package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liang
 */
@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Resource
    private DishService dishService;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    //使用redis缓存查询, 如果缓存中存在, 直接返回缓存数据, 否则查询数据库
    @Cacheable(cacheNames = "dishCache", key = "#a0")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("根据分类id查询菜品:{}", categoryId);
        List<DishVO> list = dishService.listWithFlavor(categoryId);
        return Result.success(list);
    }

}
