package com.itheima.reggie.controller;

/**
 * @author amass_
 * @date 2021/10/17
 */

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据type的值判断增加的是菜品还是套餐
     *
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("Category{}:", category);

        categoryService.save(category);

        return R.success("新增分类成功");

    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<IPage<Category>> list(int page, int pageSize) {
        return categoryService.ifndPageAndName(page, pageSize);
    }

    /**
     * 跟新分类信息
     *
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.update(category);
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> delete(Long id) {
        categoryService.delete(id);

        return R.success("分类信息删除成功");
    }
}
