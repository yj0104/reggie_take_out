package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrderDetailMapper;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author amass_
 * @date 2021/10/22
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 购物车结算
     *
     * @param orders
     */
    @Override
    public void orderSubmit(Orders orders) {
        //获取当前用户购物车数据,判断是否为空
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("当前购物车为空,无法结账");
        }

        //查询用户数据
        User user = userService.getById(userId);

        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("用户地址信息有误,不能下单");
        }

        //生成一个订单号,该订单号是唯一的
        long orderId = IdWorker.getId();

        //用于计算金额
        AtomicInteger amount = new AtomicInteger(0);

        //组装订单明细信息,就是把该表需要的字段Set给它
        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            //设置订单号
            orderDetail.setOrderId(orderId);
            //设置金额
            orderDetail.setNumber(item.getNumber());
            //设置菜品口味信息
            orderDetail.setDishFlavor(item.getDishFlavor());
            //设置菜品id
            orderDetail.setDishId(item.getDishId());
            //设置套餐id
            orderDetail.setSetmealId(item.getSetmealId());
            //设置用户名
            orderDetail.setName(item.getName());
            //设置图片信息
            orderDetail.setImage(item.getImage());
            //设置总金额
            orderDetail.setAmount(item.getAmount());
            //计算总金额
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        //组装订单信息
        //设置id
        orders.setId(orderId);
        //设置下单时间
        orders.setOrderTime(LocalDateTime.now());
        //设置结账时间
        orders.setCheckoutTime(LocalDateTime.now());
        //设置支付方式
        orders.setStatus(2);
        //设置总金额
        orders.setAmount(new BigDecimal(amount.get()));
        //设置下单用户id
        orders.setUserId(userId);
        //设置下单总金额
        orders.setNumber(String.valueOf(orderId));
        //设置下单用户民
        orders.setUserName(user.getName());
        //设置手机号
        orders.setConsignee(addressBook.getPhone());
        //设置住址信息
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        //向订单表拆入数据,一条数据
        this.save(orders);

        //向订单明细表插入数据,多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);

    }

    /**
     * 查询订单
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public IPage<OrdersDto> ordersUserPage(int page, int pageSize) {
        //1.根据用户Id查询Orders表
        Long userId = BaseContext.getCurrentId();
        //2.分页构造器
        IPage<Orders> ordersIPage = new Page<>();
        //3.根据userId查询orders
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getUserId, userId);
        ordersIPage = orderMapper.selectPage(ordersIPage, ordersLambdaQueryWrapper);

        //10.创建一个OrdersDto泛型的构造器
        IPage<OrdersDto> ordersDtoIPage = new Page<>(page, pageSize);
        //5.获取分页构造器里的Orders数据表
        List<Orders> records = ordersIPage.getRecords();
        //6.获取每个对象的ordersId
        List<OrdersDto> ordersDtoList = records.stream().map(itme -> {
            //4.根据OrdersId查询OrderDetail表的数据
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            //6.1获取id
            Long ordersId = itme.getId();
            //7.设置SQL条件
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, ordersId);
            //8.查询结果
            List<OrderDetail> orderDetail = orderDetailMapper.selectList(orderDetailLambdaQueryWrapper);
            //9.将结果设置进OrdersDto
            OrdersDto ordersDto = new OrdersDto();
            ordersDto.setOrderDetails(orderDetail);
            //11.将Orders拷贝进Dto
            BeanUtils.copyProperties(itme, ordersDto);
            //12.将Dto对象返回
            return ordersDto;
        }).collect(Collectors.toList());
        //13.将Dto数据设置进分页构造器
        ordersDtoIPage.setRecords(ordersDtoList);
        return ordersDtoIPage;


/*        //对Orders分页构造
        IPage<Orders> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //对查询出来的订单排序,根据成交时间排序
        queryWrapper.orderByDesc(Orders::getOrderTime);
        this.page(pageInfo, queryWrapper);

        //构造OrdersDto分页
        IPage<OrdersDto> dtoIPage = new Page<>();
        //BeanUtils.copyProperties(pageInfo,dtoIPage,"record");
        BeanUtils.copyProperties(pageInfo,dtoIPage);
        //获取分页记录列表
        List<Orders> records = pageInfo.getRecords();
        //根据ordersId查询OrderDetail表
        List<OrdersDto> list = records.stream().map(item ->{
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            Long id = item.getId();
            LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(OrderDetail::getOrderId,id);
            List<OrderDetail> orderDetails = orderDetailMapper.selectList(queryWrapper1);
            //设置订单明细
            ordersDto.setOrderDetails(orderDetails);
            return ordersDto;
        }).collect(Collectors.toList());
        //将根据ordersId查询出来的OrderDetail表设置进OrdersDto
        dtoIPage.setRecords(list);
        return dtoIPage;*/
    }

    @Override
    public IPage<Orders> ordersPage(int page, int pageSize, Long number, Date beginTime, Date endTime) {
        IPage<Orders> ordersIPage = new Page<>(page, pageSize);
       Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据用户id查询
        ordersLambdaQueryWrapper.eq(Orders::getUserId, userId);
        ordersLambdaQueryWrapper.eq(number != null, Orders::getId, number);
        ordersLambdaQueryWrapper.between(beginTime != null || endTime != null, Orders::getCheckoutTime, beginTime, endTime);
        ordersIPage = orderMapper.selectPage(ordersIPage, ordersLambdaQueryWrapper);
        return ordersIPage;
    }
}
