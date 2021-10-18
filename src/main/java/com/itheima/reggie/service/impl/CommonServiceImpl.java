package com.itheima.reggie.service.impl;

import com.itheima.reggie.common.R;
import com.itheima.reggie.service.CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author amass_
 * @date 2021/10/17
 *
 * 通用管理类
 */
@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

    //通过该注解可以获取到application.yml配置文件内对应的属性
    @Value("${reggie.path}")
    private String basePath;


    /**
     * 下载图片
     *
     * @param file;前端传过来的图片,其内文件是临时的
     * @return;返回文件名
     */
    @Override
    public R<String> upload(MultipartFile file) {
        //file是一个临时文件,需要转存到指定位置,否则本次请求完成后临时文件会删除
        log.info(file.toString());

        //先获取原始文件名,获取文件后缀
        String originalFilename = file.getOriginalFilename();
        //substring该方法会返回指定索引之间的字符串,参数列表(开始索引,结束索引)注:结束索引不指定默认字符串最后一位
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新给文件命名,反之数据过多后文件重名,然后在拼接获取的文件后缀
        String fileName = UUID.randomUUID().toString() + suffix;

        //创建一个目录对象,判断指定的文件路径是否存在,不存在就创建
        File dir = new File(basePath);
        //exists方法用于判断文件目录是否存在
        if (!dir.exists()) {
            //目录不存在,创建
            dir.mkdirs();
        }

        //将临时文件转存到指定位置
        try {
            //该方法用于转存,参数列表里表示转存目的地;
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //考虑到前端可能会用到文件名,将文件名传递回去
        return R.success(fileName);
    }

    /**
     * 下载文件
     *
     * @param name;需要的文件名
     * @param response
     */
    @Override
    public void download(String name, HttpServletResponse response) {
        try {
            //输入流,通过输入流读取文件内容
            FileInputStream fis = new FileInputStream(new File(basePath + name));

            //输出流,通过输出流将文件写回浏览器
            ServletOutputStream os = response.getOutputStream();

            //设置此次相应的数据类型
            response.setContentType("image/jpeg");

            //将文件写出
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fis.read(bytes)) != -1){
                os.write(bytes,0,len);
            }
            //手动创建的需要手动关闭资源,其他的不需要;
//            os.close();
//            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
