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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addShopingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);

        shoppingCart.setUserId(BaseContext.getCurrentId());
        if (shoppingCart.getDishFlavor() != null && shoppingCart.getDishFlavor().trim().isEmpty()) {
            shoppingCart.setDishFlavor(null);
        }

        // 1、查询当前菜品或套餐是否在购物车中
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList != null && !shoppingCartList.isEmpty()) {
            // 已存在，数量加1
             ShoppingCart cart = shoppingCartList.get(0);
             cart.setNumber(cart.getNumber() + 1);
             shoppingCartMapper.updateNumberById(cart);
             return;
        }

        // 2、不存在，判断是菜品还是套餐
        if (shoppingCart.getDishId() != null) {
            Dish dish = dishMapper.getById(shoppingCart.getDishId());
            if (dish == null) {
                throw new RuntimeException("菜品不存在");
            }
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
        }
        else if (shoppingCart.getSetmealId() != null) {
            Setmeal setmeal = setmealMapper.getById(shoppingCart.getSetmealId());
            if (setmeal == null) {
                throw new RuntimeException("套餐不存在");
            }
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());
        }
        else {
            throw new RuntimeException("参数错误：dishId和setmealId不能同时为空");
        }

        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartMapper.insert(shoppingCart);
    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        return shoppingCartMapper.list(ShoppingCart.
                builder().
                userId(BaseContext.getCurrentId()).
                build());
    }

    @Override
    public void cleanShoppingCart() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }
}
