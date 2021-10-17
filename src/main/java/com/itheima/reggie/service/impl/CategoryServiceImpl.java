package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author amass_
 * @date 2021/10/17
 */
@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 分页查询及根据sort排序
     *
     * @param page;当前页码
     * @param pageSize;每页显示长度
     * @return
     */
    @Override
    public R<IPage<Category>> ifndPageAndName(int page, int pageSize) {
        //MyBatisPlus提供的分页查询工具类
        IPage<Category> p = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        //添加排序条件,根据sori进行排序
        lqw.orderByAsc(Category::getSort);
        //分页查询
        IPage<Category> categoryIPage = categoryMapper.selectPage(p, lqw);
        return R.success(categoryIPage);
    }

    /**
     * 修改分类
     *
     * @param category
     * @return
     */
    @Override
    public void update(Category category) {
        //日志打印信息
        log.info(category.toString(), "修改分类");
        categoryMapper.updateById(category);
    }

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @Override
    public void delete(Long id) {
        //打印日志
        log.info("要删除的id是:" + id);

        //添加查询条件，根据分类id进行查询菜品数据
        LambdaQueryWrapper<Dish> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(Dish::getCategoryId, id);
        Integer count1 = dishMapper.selectCount(lqw1);
        if (0 < count1) {
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(Setmeal::getCategoryId, id);
        Integer count2 = setmealMapper.selectCount(lqw2);
        if (0 < count2) {
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        //如果都没事,正常删除
        categoryMapper.deleteById(id);
        //日志提示
        log.info("删除成功",id);
    }
}