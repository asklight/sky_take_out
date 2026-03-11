package com.sky.controller.admin;

import com.sky.constant.FileUploadConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用接口
 */

@Api(tags = "通用接口")
@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @ApiOperation(value = "文件上传接口")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);

        //思路：
        //1.获取文件原始名称
        String originalFilename = file.getOriginalFilename();
        //获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //判断文件后缀是否合法（jpg、jpeg、png、gif）
        if (!suffix.equalsIgnoreCase(".jpg") && !suffix.equalsIgnoreCase(".jpeg") && !suffix.equalsIgnoreCase(".png") && !suffix.equalsIgnoreCase(".gif")) {
            return Result.error("文件格式不合法");
        }
        //2.生成新的文件名称（UUID + 原始文件后缀）
        String newFileName = java.util.UUID.randomUUID().toString() + suffix;
        //3.将文件保存到指定位置
        String filePath = FileUploadConstant.FILE_UPLOAD_DIR + newFileName;
        try {
            file.transferTo(new java.io.File(filePath));
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败");
        }
        //4.返回文件访问的URL地址
        return Result.success(FileUploadConstant.FILE_ACCESS_PATH_PREFIX + newFileName);
    }
}
