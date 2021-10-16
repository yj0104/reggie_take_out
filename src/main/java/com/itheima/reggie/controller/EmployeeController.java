package com.itheima.reggie.controller;

/**
 * @author amass_
 * @date 2021/10/14
 */

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 用户登录
     * @param request
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
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        R<String> insert = employeeService.insert(request, employee);
        return insert;
    }
}