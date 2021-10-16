package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    @TableField(fill = FieldFill.INSERT)
    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    //表示在插入时填充该属性值
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //表示插入/更新时填充该属性值
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
