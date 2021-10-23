package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author amass_
 * @date 2021/10/21
 * <p>
 * 购物车
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> shoppingCartList() {
        log.info("查看购物车");
        List<ShoppingCart> list = shoppingCartService.shoppingCartList();
        return R.success(list);
    }


    @PostMapping("/add")
    public R<ShoppingCart> shoppingCartAdd(@RequestBody ShoppingCart shoppingCart) {
        ShoppingCart shoppingCart1 = shoppingCartService.shoppingCartAdd(shoppingCart);
        return R.success(shoppingCart1);
    }

    @PostMapping("/sub")
    public R<ShoppingCart> shoppingCartSub(@RequestBody ShoppingCart shoppingCart) {
        ShoppingCart shoppingCart1 = shoppingCartService.shoppingCartSub(shoppingCart);

        return R.success(shoppingCart1);
    }

    @DeleteMapping("/clean")
    public R<String> shoppingCartclean(){
        //根据用户id删除购物车

        shoppingCartService.shoppingCartclean();
        return R.success("删除成功");
    }
}
