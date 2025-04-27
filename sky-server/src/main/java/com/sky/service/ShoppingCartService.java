package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-27-15:12
 * @Description
 */
public interface ShoppingCartService {

   /**
    * 将菜品或套餐添加到购物车中
    * @param shoppingCartDTO
    */
   void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

   /**
    * 查看用户的购物车信息
    * @return
    */
   List<ShoppingCart> listShoppingCarts();

   /**
    * 清空用户的购物车
    */
   void cleanShoppingCart();
}
