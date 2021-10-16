package com.itheima.reggie.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
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

    /**
     * 分页查询
     * @param name 员工姓名
     * @param page  第几页
     * @param pageSize  每页显示长度
     * @return
     */
    R<IPage<Employee>> ifndPageAndName(String name, int page, int pageSize);

    /**
     * 更新用户信息
     * @param request
     * @param employee
     * @return
     */
    int update(HttpServletRequest request, Employee employee);

    /**
     * 根据id查询用户
     * @param id;定位查询
     */
    R<Employee> selectById(Long id);
}
