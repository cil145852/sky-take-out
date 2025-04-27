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

   /**
    * 删除购物车中一个商品
    * 如果购物车中数量为1，则直接删除
    * 如果购物车中数量大于1，则数量减一
    * @param shoppingCartDTO
    */
   void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
