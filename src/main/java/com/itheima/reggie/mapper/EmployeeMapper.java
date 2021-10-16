package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author amass_
 * @date 2021/10/14
 */
//继承BaseMapper，由该类提供基础的CRUD方法;
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {


}
