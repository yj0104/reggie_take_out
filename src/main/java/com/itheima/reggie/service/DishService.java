package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

import java.util.List;

/**
 * @author amass_
 * @date 2021/10/17
 */
public interface DishService extends IService<Dish> {
    /**
     * 新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 分页查询,涉及多表查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    R<Page> dishPage(int page, int pageSize, String name);

    /**
     * 根据id查询菜品信息和对应的口味信息
     */
    R<DishDto> getByIdWithFlavor(Long id);

    /**
     * 修改菜品以及口味信息
     */
    void updateWithFlavor(DishDto dishDto);

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    R<List<DishDto>> list(Dish dish);

    /**
     * 根据id删除
     * @param ids
     */
    void dishDelete(Long[] ids);

    /**
     * 批量修改停售起售状态
     * @param ids
     * @param i
     */
    void dishStatus(Long[] ids, int i);
}
