package com.itheima.reggie.service;

import com.itheima.reggie.common.R;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author amass_
 * @date 2021/10/17
 */
public interface CommonService {
    /**
     * 上传图片
     * @param file
     * @return
     */
    R<String> upload(MultipartFile file);

    /**
     * 下载图片
     * @param name
     * @param response
     */
    void download(String name, HttpServletResponse response);
}
