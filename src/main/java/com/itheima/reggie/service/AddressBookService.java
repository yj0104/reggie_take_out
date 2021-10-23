package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.AddressBook;

import java.util.List;

/**
 * @author amass_
 * @date 2021/10/21
 */
public interface AddressBookService extends IService<AddressBook> {


    /**
     * 查询用户地址
     * @return
     */
    List<AddressBook> addressBookList();

    /**
     * 更新默认地址
     * @param addressBook
     */
    void addressBookUpdataDefault(AddressBook addressBook);

    /**
     * 查询用户默认地址
     */
    AddressBook addressBookGetDefault();
}
