package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-27-15:14
 * @Description
 */
@Mapper
public interface ShoppingCartMapper {
   /**
    * 根据条件查询购物车数据
    * @param shoppingCart
    * @return
    */
   List<ShoppingCart> selectList(ShoppingCart shoppingCart);

   /**
    * 插入购物车数据
    * @param shoppingCart
    */
   void insert(ShoppingCart shoppingCart);

   /**
    * 更新购物车数据
    * @param shoppingCart
    */
   void update(ShoppingCart shoppingCart);

   /**
    * 根据用户id删除购物车数据
    * @param userId
    */
   void deleteByUserId(Long userId);
}
