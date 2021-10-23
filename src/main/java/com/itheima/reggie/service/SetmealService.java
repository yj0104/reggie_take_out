package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;

import java.util.List;

/**
 * @author amass_
 * @date 2021/10/17
 */
public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐,同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 分页查询
     */
    Page page(int page, int pageSize, String name);

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     *
     * @param ids
     */
    void removeWithDish(List<Long> ids);

    /**
     * 条件查询
     *
     * @param setmeal
     * @return
     */
    List<Setmeal> setmealList(Setmeal setmeal);

    /**
     * 查询套餐详细菜品
     * @param id
     * @return
     */
    List<SetmealDish> getSetmealDish(Long id);


    /**
     * 批量修改停售起售
     * @param ids
     * @param i
     */
    void setmealUpdatestatus(Long[] ids, int i);


    /**
     * 根据id查询
     * @param id
     */
    SetmealDto getSetmeal(Long id);
}
