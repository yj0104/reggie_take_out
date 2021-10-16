package com.itheima.reggie.controller;

/**
 * @author amass_
 * @date 2021/10/14
 */

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 用户登录
     *
     * @param request;客户端的请求都会封装在这个对象中
     * @param employee;接收前端传递给后端的json字符串中的数据的(请求体中的数据的)；
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        R<Employee> result = employeeService.login(employee);
        if (result.getCode() == 1) {
            //如果登录成功，则在session中保存登陆状态
            request.getSession().setAttribute("userInfo", result.getData().getId());
        }
        return result;
    }

    /**
     * 用户退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        employeeService.logout(request);
        return R.success("退出成功");
    }

    /**
     * 新增用户
     * @param request;客户端的请求都会封装在这个对象中
     * @param employee;接收前端传递给后端的json字符串中的数据的(请求体中的数据的)；
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        R<String> insert = employeeService.insert(request, employee);
        return insert;
    }

    /**
     * 分页查询
     *
     * @param name     员工姓名-可选参数
     * @param page     当前显示页码
     * @param pageSize 每页展示记录数
     * @return
     */
    @GetMapping("/page")
    public R<IPage<Employee>> list(String name, int page, int pageSize) {
        return employeeService.ifndPageAndName(name, page, pageSize);
    }

    /**
     * 更新用户状态
     *
     * @param request;客户端的请求都会封装在这个对象中
     * @param employee;接收前端传递给后端的json字符串中的数据的(请求体中的数据的)；
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        //日志信息打印
        log.info(employee.toString());
        //无聊地发泄balabalabala;
        int update = employeeService.update(request, employee);
        if (update == 0) {
            return R.error("员工信息修改失败");
        }
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询数据
     *
     * @param id;定位查询
     * @return
     */
    @GetMapping("{id}")
    public R<Employee> selectById(@PathVariable Long id) {
        R<Employee> employeeR = employeeService.selectById(id);
        return employeeR;
    }
}