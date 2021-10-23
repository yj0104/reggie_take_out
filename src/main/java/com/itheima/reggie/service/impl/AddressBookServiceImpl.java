package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.mapper.AddressBookMapper;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author amass_
 * @date 2021/10/21
 */
@Service
@Slf4j
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 根据用户id查询所属地址;
     *
     * @return
     */
    @Override
    public List<AddressBook> addressBookList() {
        //根据当前登录的ID查询对应的地址
        Long userId = BaseContext.getCurrentId();
        log.info("userId={}", userId);
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(userId != null, AddressBook::getUserId, userId);
        addressBookLambdaQueryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBooks = addressBookMapper.selectList(addressBookLambdaQueryWrapper);
        return addressBooks;
    }

    @Override
    public void addressBookUpdataDefault(AddressBook addressBook) {
        Long id = addressBook.getId();
        //默认地址每个用户只用一个,先把对应的全部地址改成不默认(0)
        Long userId = BaseContext.getCurrentId();
        LambdaUpdateWrapper<AddressBook> addressBookLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        //设置SQL,将所有userId下的IsDefault改为0,
        addressBookLambdaUpdateWrapper.set(AddressBook::getIsDefault, 0);
        addressBookLambdaUpdateWrapper.eq(AddressBook::getUserId, userId);
        addressBookMapper.update(null, addressBookLambdaUpdateWrapper);


        addressBook.setIsDefault(1);
        addressBookMapper.updateById(addressBook);
/*        LambdaUpdateWrapper<AddressBook> addressBookLambdaUpdateWrapper2 = new LambdaUpdateWrapper<>();
        addressBookLambdaUpdateWrapper2.set(AddressBook::getIsDefault,1);
        addressBookLambdaUpdateWrapper2.eq(AddressBook::getId,id);
        addressBookMapper.update(null,addressBookLambdaUpdateWrapper2);*/
    }

    /**
     * 查询用户默认地址
     */
    @Override
    public AddressBook addressBookGetDefault() {
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(AddressBook::getUserId,currentId);
        addressBookLambdaQueryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookMapper.selectOne(addressBookLambdaQueryWrapper);
        return addressBook;

    }
}