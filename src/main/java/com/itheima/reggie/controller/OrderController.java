package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrderService;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author amass_
 * @date 2021/10/22
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/submit")
    public R<String> orderSubmit(@RequestBody Orders orders) {
        orderService.orderSubmit(orders);
        return R.success("chengg");
    }

    /**
     * 用户端分页查询订单
     */
    @GetMapping("/userPage")
    public R<IPage<OrdersDto>> ordersUserPage(int page, int pageSize) {
        IPage<OrdersDto> dtoIPage = orderService.ordersUserPage(page, pageSize);
        return R.success(dtoIPage);
    }


    /**
     * 管理端用户查询订单
     */
    @GetMapping("/page")
    public R<IPage<Orders>> ordersPage(int page, int pageSize, Long number, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        IPage<Orders> ordersIPage = orderService.ordersPage(page, pageSize, number, beginTime, endTime);
        return R.success(ordersIPage);

    }


    /**
     * 管理端跟新订单状态
     */
    @PutMapping
    public R<String> ordersUpdate(@RequestBody Orders orders) {
        orderService.updateById(orders);
        return R.success("success");
    }


    /**
     * 再来一单
     */
    @PostMapping("/again")
    public R<String> ordersRecur(@RequestBody Orders orders) {
        Long id = orders.getId();
        //查询订单明细表
        List<OrderDetail> orderDetails = orderDetailService.ordersRecur(id);
        //将数据添加进购物车
        shoppingCartService.ordersRecur(orderDetails);
        return R.success("success");
    }
}
