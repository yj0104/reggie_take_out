package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.mapper.DishFlavorMapper;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author amass_
 * @date 2021/10/17
 *
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private CategoryMapper categoryMapper;


    /**
     * 新增菜品以及口味信息
     * 由于在saveWithFlavor方法中，进行了两次数据库的保存操作.
     * 操作了两张表，那么为了保证数据的一致性，我们需要在方法上加上注解 @Transactional来控制事务。
     *
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {

        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        //菜品id
        Long dishId = dishDto.getId();
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        if (flavors != null) {
            flavors.stream().map((item) -> {
                item.setDishId(dishId);
                return item;
            }).collect((Collectors.toList()));
        }
        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 分页查询
     * @return
     */
    @Override
    public R<Page> dishPage(int page, int pageSize, String name) {
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        //添加过滤条件
        lqw.like(name != null,Dish::getName,name);
        //添加排序条件
        lqw.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        page(pageInfo,lqw);

        //获取每个dish对象
        List<Dish> records = pageInfo.getRecords();
        for (Dish dish : records) {
            //根据分类id查询分类名称
            LambdaQueryWrapper<Category> lqw1 = new LambdaQueryWrapper<>();
            lqw1.eq(Category::getId,dish.getCategoryId());
            Category category = categoryMapper.selectOne(lqw1);
            String categoryName = category.getName();
            //设置给dish对象
            dish.setCategoryName(categoryName);
        }
        return R.success(pageInfo);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     */
    @Override
    public R<DishDto> getByIdWithFlavor(Long id) {
        //1.根据id查询相应菜品
        Dish byId = this.getById(id);

        //2.据id获取对应的菜品口味信息
        //3.设置SQL语句
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();

        lqw.eq(DishFlavor::getDishId,id);
        //4.执行语句获取返回值
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(lqw);

        //5.获取后封装进dishDto
        DishDto dishDto = new DishDto();
        dishDto.setFlavors(dishFlavors);

        //6.将Dish数据复制给DishDto
        BeanUtils.copyProperties(byId,dishDto);
        return R.success(dishDto);
    }

    /**
     * 修改菜品以及口味信息
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //1.更新dish表
        this.updateById(dishDto);
        Long id = dishDto.getId();
        //2.更新DishFlavor表
        //3.先获取DishFlavor表
        List<DishFlavor> flavors = dishDto.getFlavors();
        //4.由于口味表里的字段更新可能是增加也可能是删除,所以直接删除重建操作简单很多
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        //因为DishDto表继承于Dish,前者里没有的字段会去后者里找
        lqw.eq(DishFlavor::getDishId,id);
        dishFlavorMapper.delete(lqw);
        if (flavors != null) {
            flavors.stream().map((item) -> {
                item.setDishId(id);
                return item;
            }).collect((Collectors.toList()));
        }
        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }
}
