package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author amass_
 * @date 2021/10/14
 */
@Service
public interface EmployeeService extends IService<Employee> {
    /**
     *登录
     * @param employee
     * @return
     */
    R<Employee> login(Employee employee);

    /**
     * 退出登录
     * @param request
     */
    void logout(HttpServletRequest request);

    /**
     * 添加员工
     * @param request
     * @param employee
     */
    R<String> insert(HttpServletRequest request, Employee employee);
}
