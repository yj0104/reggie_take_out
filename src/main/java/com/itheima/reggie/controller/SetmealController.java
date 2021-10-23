package com.itheima.reggie.controller;

/**
 * @author amass_
 * @date 2021/10/20
 */

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息{}", setmealDto);
        setmealService.saveWithDish(setmealDto);

        return R.success("添加数据成功");
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> pageSetmeal(int page, int pageSize, String name) {
        Page page1 = setmealService.page(page, pageSize, name);
        return R.success(page1);
    }

    /**
     * 根据id删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("要删除的ID是{}", ids);
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 根据条件查询
     *
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> setmealList(Setmeal setmeal) {
        List<Setmeal> setmeals = setmealService.setmealList(setmeal);
        return R.success(setmeals);
    }


    /**
     * 查询套餐详细菜品
     *
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<SetmealDish>> setmealGetDish(@PathVariable Long id) {
        log.info("ID={}", id);
        List<SetmealDish> setmealDish = setmealService.getSetmealDish(id);
        return R.success(setmealDish);
    }


    /**
     * 批量修改停售起售
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> setmealUpdatestatus1(Long[] ids) {
        setmealService.setmealUpdatestatus(ids, 1);
        return R.success("success");
    }

    /**
     * 批量修改停售起售
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> setmealUpdatestatus0(Long[] ids) {
        setmealService.setmealUpdatestatus(ids, 0);
        return R.success("success");
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public R<SetmealDto> getSetmeal(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getSetmeal(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     *
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> updataSetmeal(@RequestBody SetmealDto setmealDto) {
        //更新setmeal表字段
        setmealService.updateById(setmealDto);
        //跟新setmealDish表字段
        setmealDishService.updataSetmeal(setmealDto);
        return R.success("success");
    }


}