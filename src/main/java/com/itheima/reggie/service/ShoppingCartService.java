package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.ShoppingCart;

import java.util.List;

/**
 * @author amass_
 * @date 2021/10/21
 */
public interface ShoppingCartService extends IService<ShoppingCart> {

    /**
     * 查看购物车
     */
    List<ShoppingCart> shoppingCartList();

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    ShoppingCart shoppingCartAdd(ShoppingCart shoppingCart);

    /**
     * 购物车减量
     * @param shoppingCart
     */
    ShoppingCart shoppingCartSub(ShoppingCart shoppingCart);

    /**
     * 根据用户id删除所属购物车内所有信息
     * @param
     */
    void shoppingCartclean();


    /**
     * 再来一单
     */
    void ordersRecur(List<OrderDetail> orderDetails);
}
