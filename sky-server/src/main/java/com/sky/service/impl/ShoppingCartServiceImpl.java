package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-27-15:12
 * @Description
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Resource
    private ShoppingCartMapper shoppingCartMapper;

    @Resource
    private DishMapper dishMapper;

    @Resource
    private SetmealMapper setmealMapper;

    /**
     * 将菜品或套餐添加到购物车中
     *
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //先查询购物车中是否已经存在该菜品/套餐
        ShoppingCart shoppingCart = getShoppingCartByDTO(shoppingCartDTO);
        if (shoppingCart.getId() == null) {
            //如果不存在，则将菜品/套餐添加到购物车中
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            if (shoppingCart.getDishId() != null) {
                //如果添加的是菜品
                Dish dish = dishMapper.selectByIds(Collections.singletonList(shoppingCart.getDishId())).get(0);
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
            } else {
                //如果添加的是套餐
                Setmeal setmeal = setmealMapper.selectByIds(Collections.singletonList(shoppingCart.getSetmealId())).get(0);
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCartMapper.insert(shoppingCart);
        } else {
            //如果存在，则将菜品/套餐的数量加1
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCartMapper.update(shoppingCart);
        }
    }

    /**
     * 查看用户的购物车信息
     *
     * @return
     */
    @Override
    public List<ShoppingCart> listShoppingCarts() {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();
        return shoppingCartMapper.selectList(shoppingCart);
    }

    /**
     * 清空用户的购物车
     */
    @Override
    public void cleanShoppingCart() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }

    /**
     * 删除购物车中一个商品
     * 如果购物车中数量为1，则直接删除
     * 如果购物车中数量大于1，则数量减一
     * @param shoppingCartDTO
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //要获取商品的数量信息，所以要查询购物车中该商品的信息
        ShoppingCart shoppingCart = getShoppingCartByDTO(shoppingCartDTO);

        if (shoppingCart.getId() != null) {
            if (shoppingCart.getNumber() == 1) {
                shoppingCartMapper.deleteById(shoppingCart.getId());
            } else {
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.update(shoppingCart);
            }
        } else {
            throw new RuntimeException("购物车中没有该商品");
        }

    }

    private ShoppingCart getShoppingCartByDTO(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectList(shoppingCart);

        if (ObjectUtils.isEmpty(shoppingCartList)) {
            return shoppingCart;
        }

        return shoppingCartList.get(0);
    }
}
