package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author amass_
 * @date 2021/10/17
 * <p>
 * 菜品管理
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");

    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        return dishService.dishPage(page, pageSize, name);

    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     */
    @GetMapping("/{id}")
    public R<DishDto> getByIdWithFlavor(@PathVariable Long id) {
        return dishService.getByIdWithFlavor(id);
    }

    /**
     * 修改菜品以及口味信息
     */
    @PutMapping
    public R<String> updateWithFlavor(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }


    /**
     * 根据条件查询对应的菜品数据
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {

        return dishService.list(dish);
    }

    /**
     * 根据id删除数据
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> dishDelete(Long[] ids) {
        dishService.dishDelete(ids);

        return R.success("成功");
    }

    /**
     * 修改停售起售状态
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> dishStatus1(Long[] ids) {
        dishService.dishStatus(ids,1);
        return R.success("成功");
    }

    /**
     * 修改停售起售状态
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> dishStatus0(Long[] ids) {
        dishService.dishStatus(ids,0);

        return R.success("成功");
    }
}
