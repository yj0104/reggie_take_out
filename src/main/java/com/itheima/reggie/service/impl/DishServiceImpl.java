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
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private CategoryMapper categoryMapper;


    /**
     * 新增菜品以及口味信息
     * 由于在saveWithFlavor方法中，进行了两次数据库的保存操作.
     * 操作了两张表，那么为了保证数据的一致性，我们需要在方法上加上注解 @Transactional来控制事务。
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
     *
     * @return
     */
    @Override
    public R<Page> dishPage(int page, int pageSize, String name) {
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        //添加过滤条件
        lqw.like(name != null, Dish::getName, name);
        //添加排序条件
        lqw.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        page(pageInfo, lqw);

        //获取每个dish对象
        List<Dish> records = pageInfo.getRecords();
        for (Dish dish : records) {
            //根据分类id查询分类名称
            LambdaQueryWrapper<Category> lqw1 = new LambdaQueryWrapper<>();
            lqw1.eq(Category::getId, dish.getCategoryId());
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

        lqw.eq(DishFlavor::getDishId, id);
        //4.执行语句获取返回值
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(lqw);

        //5.获取后封装进dishDto
        DishDto dishDto = new DishDto();
        dishDto.setFlavors(dishFlavors);

        //6.将Dish数据复制给DishDto
        BeanUtils.copyProperties(byId, dishDto);
        return R.success(dishDto);
    }

    /**
     * 修改菜品以及口味信息
     */
    @Override
    @Transactional
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
        lqw.eq(DishFlavor::getDishId, id);
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

    /**
     * 根据条件查询对应的菜品数据
     *
     * @param dish
     * @return
     */
    @Override
    public R<List<DishDto>> list(Dish dish) {
        //查询基本菜品信息
        //构造查询条件
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件,查询状态为1(起售状态的菜品)
        lqw.eq(Dish::getStatus, 1);
        //添加排序条件
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //收集信息
        List<Dish> dishList = dishMapper.selectList(lqw);


        //将数据拷贝至dishDto
        List<DishDto> collect = dishList.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            /**
             * BeanUtils.copyProperties(dishList, dishDto);
             * 曾经的错误,我尽然妄图将集合赋值给一个实体类,当时调试了十分钟左右发现这个错误.
             */
            //设置categoryName;
            Long categoryId = item.getCategoryId();
            Category category = categoryMapper.selectById(categoryId);
            if (category != null) {
                String name = category.getName();
                dishDto.setCategoryName(name);
            }
            //设置dishFlavors
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, id);
            List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());
        log.info("collect={}", collect.toString());

        return R.success(collect);
    }

    /**
     * 根据id删除,并且删除口味表
     *
     * @param ids
     */
    @Override
    @Transactional
    public void dishDelete(Long[] ids) {
        //删除口味数据
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(ids.length > 0, DishFlavor::getDishId, ids);
        dishFlavorMapper.delete(dishFlavorLambdaQueryWrapper);
        //删除Dish表数据
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(ids.length > 0, Dish::getId, ids);
        dishMapper.delete(dishLambdaQueryWrapper);
    }

    /**
     * 批量修改停售起售状态
     *
     * @param ids
     * @param i
     */
    @Override
    @Transactional
    public void dishStatus(Long[] ids, int i) {
        Dish dish = new Dish();
        for (Long id : ids) {
            dish.setId(id);
            dish.setStatus(i);
            dishMapper.updateById(dish);
        }
    }
}
