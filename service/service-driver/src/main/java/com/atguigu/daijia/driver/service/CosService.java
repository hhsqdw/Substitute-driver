package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.vo.driver.CosUploadVo;
import org.springframework.web.multipart.MultipartFile;

public interface CosService {

    // 获取临时密钥
    String getImageUrl(String path);

    // 文件上传
    CosUploadVo upload(MultipartFile file, String path);
}
