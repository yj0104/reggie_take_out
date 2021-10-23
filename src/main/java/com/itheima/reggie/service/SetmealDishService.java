package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.SetmealDish;

/**
 * @author amass_
 * @date 2021/10/20
 */
public interface SetmealDishService extends IService<SetmealDish> {


    /**
     * 删除后添加
     */
    void updataSetmeal(SetmealDto setmealDto);
}
