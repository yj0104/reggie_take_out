package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author amass_
 * @date 2021/10/20
 */
public interface UserService extends IService<User> {

    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    R<String> sendMsg(User user, HttpSession session);

    /**
     * 验证用户登录
     * @param map
     * @param session
     * @return
     */
    R<User> login(Map map, HttpSession session);
}
