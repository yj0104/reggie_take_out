package com.itheima.reggie.dto;

import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import lombok.Data;
import java.util.List;

//订单和订单明细;
@Data
public class OrdersDto extends Orders {

    //用户名
    private String userName;

    //手机
    private String phone;

    //地址
    private String address;

    //收货人
    private String consignee;

    //订单明细
    private List<OrderDetail> orderDetails;
	
}
