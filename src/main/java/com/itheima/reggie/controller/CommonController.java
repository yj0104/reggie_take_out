package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author amass_
 * @date 2021/10/17
 * 通用控制类
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private CommonService commonService;

    /**
     * 文件的上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {

        R<String> fileName = commonService.upload(file);

        //将文件名传递回去
        return fileName;
    }

    /**
     * 文件的下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        commonService.download(name,response);
    }

}
