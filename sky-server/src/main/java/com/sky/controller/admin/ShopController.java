package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-25-08:55
 * @Description
 */
@Slf4j
@RestController("adminShopController")
@Api(tags = "店铺相关接口")
@RequestMapping("/admin/shop")
public class ShopController {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final String KEY = "SHOP_STATUS";
    private final Integer DEFAULT_STATUS = 0;
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺的营业状态为 {}", status == 1 ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(KEY, status);
        return Result.success();
    }

    @GetMapping("/status")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        if (status == null) {
            status = DEFAULT_STATUS;
        }
        log.info("获取店铺的营业状态为 {}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
