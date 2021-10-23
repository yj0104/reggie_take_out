package com.itheima.reggie.controller;

import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author amass_
 * @date 2021/10/21
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;


    /**
     * 根据id查询用户地址
     *
     * @return
     */
    @GetMapping("/list")
    private R<List<AddressBook>> addressBookList() {
        List<AddressBook> addressBooks = addressBookService.addressBookList();
        return R.success(addressBooks);
    }

    /**
     * 添加用户收快递地址
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> addressBookSave(@RequestBody AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success("添加地址成功");
    }

    /**
     * 用户设置默认地址
     *
     * @return
     */
    @PutMapping("/default")
    public R<String> addressBookUpdataDefault(@RequestBody AddressBook addressBook) {
        addressBookService.addressBookUpdataDefault(addressBook);
        return R.success("默认地址修改成功");
    }

    /**
     * 查询用户默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> addressBookGetDefault() {
        AddressBook addressBook = addressBookService.addressBookGetDefault();
        return R.success(addressBook);
    }


    @GetMapping("{id}")
    public R<AddressBook> addressBookGet(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }


    @PutMapping
    public R<String> addressBookPut(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("成功");
    }

    /**
     * 根据id删除地址
     */
    @DeleteMapping
    public R<String> addressBookDelete(Long ids){
        addressBookService.removeById(ids);
        return R.success("success");
    }
}
