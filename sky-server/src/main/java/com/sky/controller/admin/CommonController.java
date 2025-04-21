package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;

/**
 * @author liang
 * @version 1.0
 * @CreateDate 2025-04-21-20:23
 * @Description 通用控制器用于实现一些通用的接口
 */
@Slf4j
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
public class CommonController {

    @Resource
    private AliOssUtil aliOssUtil;
    /**
     * 文件上传
     * @param multipartFile
     * @return
     */
    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile multipartFile) {
        log.info("文件上传：{}", multipartFile);
        try {
            String filename = UUID.randomUUID().toString().replaceAll("-", "") + "-" +
                    System.currentTimeMillis() + "." +
                    multipartFile.getOriginalFilename();
            String fileUrl = aliOssUtil.upload(multipartFile.getBytes(), filename);
            log.info("文件上传成功，{}", fileUrl);
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败 {}", e.getMessage());
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
