package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@RestController("userShopController")
@Api(tags = "C端-店铺相关接口")
@RequestMapping("/user/shop")
public class ShopController {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private final String KEY = "SHOP_STATUS";
    private final Integer DEFAULT_STATUS = 0;

    /**
     * 获取店铺的营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺的营业状态")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        if (status == null) {
            status = DEFAULT_STATUS;
        }
        log.info("获取店铺的营业状态为 {}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
