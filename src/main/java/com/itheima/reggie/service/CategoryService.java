package com.itheima.reggie.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;

/**
 * @author amass_
 * @date 2021/10/17
 */
public interface CategoryService  extends IService<Category> {


    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    R<IPage<Category>> ifndPageAndName(int page, int pageSize);

    /**
     * 更新属性
     * @param category
     * @return
     */
    void update(Category category);

    /**
     * 根据id删除
     * @param id
     * @return
     */
    void delete(Long id);

}
