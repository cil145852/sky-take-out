package com.sky.service;

import com.sky.dto.ShoppingCartDTO;

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
}
