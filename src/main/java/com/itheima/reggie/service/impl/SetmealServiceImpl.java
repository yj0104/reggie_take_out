package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.mapper.SetmealDishMapper;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author amass_
 * @date 2021/10/17
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;


    /**
     * 新增套餐
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息,操作setmeal,执行insert操作
        setmealMapper.insert(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联信息,炒作setmeal_dish,执行insert操作
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDishMapper.insert(setmealDish);
        }
    }

    /**
     * 分页查询
     */
    @Override
    public Page page(int page, int pageSize, String name) {
        //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> SLQW = new LambdaQueryWrapper<>();
        //添加查询条件,根据name进行like模糊查询
        SLQW.like(name != null, Setmeal::getName, name);
        //添加排序条件,根据更新时间降序排列
        SLQW.orderByDesc(Setmeal::getUpdateTime);

        setmealMapper.selectPage(pageInfo, SLQW);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item, setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryMapper.selectById(categoryId);
            if (category != null) {
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return dtoPage;
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //select count(*) from setmeal where id in (1,2,3) and status = 1
        //先判断id对应的套餐是否在售,在售的话返回错误
        LambdaQueryWrapper<Setmeal> setmeallqw = new LambdaQueryWrapper<>();
        //SQL语句,in代表包含
        setmeallqw.in(Setmeal::getId, ids);
        setmeallqw.eq(Setmeal::getStatus, 1);
        Integer count = setmealMapper.selectCount(setmeallqw);
        if (count > 0) {
            //如果不能删除,抛出一个业务异常
            throw new CustomException("有套餐正在售卖中,不能删除");
        }
        //如果可以删除,先删除套餐表中的数据---setmeal
        setmealMapper.deleteBatchIds(ids);
        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.in(SetmealDish::getSetmealId, ids);
        //删除关系表中的数据,----setmeal_dish
        setmealDishMapper.delete(lqw);
    }

    /**
     * 根据Id查询
     */
    @Override
    public List<Setmeal> setmealList(Setmeal setmeal) {
        //根据id查询,排除Status为0的数据,查询结果用更新时间排序
        Long categoryId = setmeal.getCategoryId();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, categoryId);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmeals = setmealMapper.selectList(setmealLambdaQueryWrapper);
        return setmeals;
    }

    /**
     * 查询套餐详细菜品
     */
    @Override
    public List<SetmealDish> getSetmealDish(Long id) {
        //查询SetmealDish表
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(setmealDishLambdaQueryWrapper);
        //获取Dish表的id,查询对应图片
        List<SetmealDish> collect = setmealDishes.stream().map(item -> {
            Dish dish = dishMapper.selectById(item.getDishId());
            item.setImage(dish.getImage());
            return item;
        }).collect(Collectors.toList());

        log.info(collect.toString());
        return collect;
    }

    /**
     * 批量修改停售起售
     */
    @Override
    public void setmealUpdatestatus(Long[] ids, int i) {
        Setmeal setmeal = new Setmeal();
        for (Long id : ids) {
            setmeal.setId(id);
            setmeal.setStatus(i);
            setmealMapper.updateById(setmeal);
        }
    }


    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public SetmealDto getSetmeal(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        //设置setmeal数据
        Setmeal setmeal = setmealMapper.selectById(id);
        BeanUtils.copyProperties(setmeal, setmealDto);
        //设置setmealDish
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDish = setmealDishMapper.selectList(setmealDishLambdaQueryWrapper);
        setmealDto.setSetmealDishes(setmealDish);
        //设置categoryName
        Long categoryId = setmeal.getCategoryId();
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Category::getId, categoryId);
        Category category = categoryMapper.selectOne(lambdaQueryWrapper);
        String name = category.getName();
        setmealDto.setCategoryName(name);
        return setmealDto;
    }
}
