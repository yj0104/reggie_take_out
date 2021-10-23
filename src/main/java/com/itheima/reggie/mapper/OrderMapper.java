package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author amass_
 * @date 2021/10/22
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
