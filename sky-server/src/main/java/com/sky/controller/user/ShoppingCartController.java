package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-27-15:07
 * @Description
 */
@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "C端-购物车接口")
@Slf4j
public class ShoppingCartController {

   @Resource
   private ShoppingCartService shoppingCartService;

   /**
    * 添加购物车
    * @param shoppingCartDTO
    * @return
    */
   @PostMapping("/add")
   @ApiOperation("添加购物车")
   public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
      shoppingCartService.addShoppingCart(shoppingCartDTO);
      return Result.success();
   }

   /**
    * 查看购物车
    * @return
    */
   @GetMapping("/list")
   @ApiOperation("查看购物车")
   public Result<List<ShoppingCart>> list() {
      return Result.success(shoppingCartService.listShoppingCarts());
   }
}
