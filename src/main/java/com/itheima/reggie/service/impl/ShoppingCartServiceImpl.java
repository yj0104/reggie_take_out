package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author amass_
 * @date 2021/10/21
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 查看购物车
     *
     * @return
     */
    @Override
    public List<ShoppingCart> shoppingCartList() {
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartLambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartMapper.selectList(shoppingCartLambdaQueryWrapper);
        return list;
    }

    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */
    @Override
    public ShoppingCart shoppingCartAdd(ShoppingCart shoppingCart) {
        //构造器对象
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //每个用户的购物车必须是单独的,根据id设置购物车
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        //判断用户添加的是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        if (dishId != null) {
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
        }
        ShoppingCart shoppingCart1 = shoppingCartMapper.selectOne(shoppingCartLambdaQueryWrapper);
        //等于空说明没有对应的购物车数据,直接添加一个新的就好
        if (shoppingCart1 != null) {
            //说明有重复的,数量加一
            Integer number = shoppingCart1.getNumber();
            shoppingCart1.setNumber(number + 1);
            shoppingCartMapper.updateById(shoppingCart1);
        } else {
            //说明没有东西,直接添加
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
            shoppingCart1 = shoppingCart;
        }
        return shoppingCart1;
    }

    /**
     * 购物车内点餐减少
     *
     * @param shoppingCart
     */
    @Override
    public ShoppingCart shoppingCartSub(ShoppingCart shoppingCart) {
        Long currentId = BaseContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //确定操作人
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        //因dishId||setmealId两个字段可能为空,加入判断
        shoppingCartLambdaQueryWrapper.eq(dishId != null, ShoppingCart::getDishId, dishId);
        shoppingCartLambdaQueryWrapper.eq(setmealId != null, ShoppingCart::getSetmealId, setmealId);


        shoppingCart = shoppingCartMapper.selectOne(shoppingCartLambdaQueryWrapper);
        //加入判断,如果数据库中的number等于1,删除数据
        Integer number1 = shoppingCart.getNumber();
        if (number1 == 1) {
            shoppingCartMapper.deleteById(shoppingCart.getId());
            shoppingCart.setNumber(0);
        } else {
            //单击一次代表减少一个
            Integer number = shoppingCart.getNumber() - 1;
            //将减少后的数值设置会实体类
            shoppingCart.setNumber(number);
        }
        //执行更新语句.
        shoppingCartMapper.updateById(shoppingCart);
        return shoppingCart;
    }

    /**
     * 根据用户id删除
     */
    @Override
    @Transactional
    public void shoppingCartclean() {
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        shoppingCartMapper.delete(shoppingCartLambdaQueryWrapper);
    }

    /**
     * 用户端再来一单
     */
    @Override
    public void ordersRecur(List<OrderDetail> orderDetails) {
        Long userId = BaseContext.getCurrentId();
        //拿出orderDetail表的数据
        List<ShoppingCart> collect = orderDetails.stream().map(item -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setName(item.getName());
            shoppingCart.setImage(item.getImage());
            shoppingCart.setUserId(userId);
            shoppingCart.setDishId(item.getDishId());
            shoppingCart.setSetmealId(item.getSetmealId());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());
        shoppingCartService.saveBatch(collect);
    }


}