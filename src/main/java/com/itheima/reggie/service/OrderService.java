package com.itheima.reggie.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.Orders;

import java.util.Date;

/**
 * @author amass_
 * @date 2021/10/22
 */
public interface OrderService extends IService<Orders> {
    /**
     * 购物车结算
     * @param orders
     */
    void orderSubmit(Orders orders);

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    IPage<OrdersDto> ordersUserPage(int page, int pageSize);


    /**
     * 分页查询
     */
    IPage<Orders> ordersPage(int page, int pageSize, Long number, Date beginTime, Date endTime);
}
