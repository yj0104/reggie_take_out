package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.OrderDetail;

import java.util.List;

/**
 * @author amass_
 * @date 2021/10/22
 */
public interface OrderDetailService extends IService<OrderDetail> {
    /**
     * 根据ordersid查询相关ordersDetail表
     * @param id
     */
    List<OrderDetail> ordersRecur(Long id);
}
