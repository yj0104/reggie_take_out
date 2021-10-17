package com.itheima.reggie.service.impl;

/**
 * @author amass_
 * @date 2021/10/14
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
@Slf4j
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;


    /**
     * 用户登录
     *
     * @param employee 封装数据的实体类
     * @return
     */
    @Override
    public R<Employee> login(Employee employee) {
        //先查询数据库里是否存在该用户名
        //使用mybatisplus提供的工具
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        //设置SQL语句
        lqw.eq(Employee::getUsername, employee.getUsername());
        //使用mybatisplus提供的工具,查询一条记录
        Employee result = employeeMapper.selectOne(lqw);
        //判断有无数据查出
        if (result == null) {
            return R.error("用户名不存在");
        }
        //先判断账号状态,减少程序运行次数
        //如果有此用户,判断是否被禁用
        if (result.getStatus() != 1) {
            return R.error("账号异常,请联系客服");
        }
        //如果账号状态正常,在判断密码是否正确
        //因为账号在数据库中被加密了,所以判断之前同样的将前端数据加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //判断密码是否错误
        if (!result.getPassword().equals(password)) {
            return R.error("用户名或密码错误");
        }

        //数据查询正常,账号状态正常,密码正常
        return R.success(result);
    }

    /**
     * 用户退出
     *
     * @param request
     */
    @Override
    public void logout(HttpServletRequest request) {
        //调用invalidate方法直接删除Session；
        request.getSession().invalidate();
    }

    /**
     * 新增加用户
     *
     * @param request
     * @param employee
     * @return
     */
    @Override
    public void insert(HttpServletRequest request, Employee employee) {
        //输出日志信息
        log.info("新增员工,员工信息:{}", employee.toString());

        //初始化密码123456,MD5加密后存入数据库
        String s = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(s);

        /**
         * 以下代码被新学的公共字段填充代替,设置id功能被ThreadLocal代替
         *         //设置创建和更新的时间
         *         employee.setCreateTime(LocalDateTime.now());
         *         employee.setUpdateTime(LocalDateTime.now());
         *
         *         //获取当前登录用户的id
         *         Long empId = (Long) request.getSession().getAttribute("userInfo");
         *
         *         //设置创建和更新的调用人
         *         employee.setCreateUser(empId);
         *         employee.setUpdateUser(empId);
         */

        //调用MP提供的增加SQL
        employeeMapper.insert(employee);
    }

    /**
     * 分页查询
     *
     * @param name     员工姓名-可选参数
     * @param page     当前查询页码
     * @param pageSize 每页显示长度
     * @return
     */
    @Override
    public R<IPage<Employee>> ifndPageAndName(String name, int page, int pageSize) {
        //MyBatisPlus提供的分页查询工具类
        IPage<Employee> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        // select * from employee where name like "%xx%"
        // 当传递name不为null&&不为空字符串&&不为空格;
        lqw.like(StringUtils.isNotBlank(name), Employee::getName, name);
        employeeMapper.selectPage(p, lqw);
        return R.success(p);
    }

    /**
     * 修改员工状态
     *
     * @param request
     * @param employee
     * @return
     */
    @Override
    public void update(HttpServletRequest request, Employee employee) {
        /**
         * 以下代码被新学的公共字段填充代替,设置id功能被ThreadLocal代替
         *         //将前端传过来的id数据进行转换,id里存的是操作人的id;
         *         Long emppId = (Long) request.getSession().getAttribute("userInfo");
         *         //设置修改词条的时间以及人员
         *         employee.setUpdateTime(LocalDateTime.now());
         *         employee.setUpdateUser(emppId);
         */
        //调用MP提供的方法;
        employeeMapper.updateById(employee);
    }


    /**
     * 根据id查询用户
     *
     * @param id;定位查询
     * @return
     */
    @Override
    public R<Employee> selectById(Long id) {
        log.info("根据id查询用户" + id);
        Employee employee = employeeMapper.selectById(id);
        return R.success(employee);
    }
}