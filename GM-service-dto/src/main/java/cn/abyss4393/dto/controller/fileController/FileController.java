package cn.abyss4393.dto.controller.fileController;

import cn.abyss4393.annotation.AuthAccess;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.service.impl.FileServiceImpl;
import cn.abyss4393.utils.imgbed.ImageBedUtils;
import cn.abyss4393.utils.imgbed.ImagePath;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className FileController
 * @description TODO
 * @date 5/9/2023
 */
@Controller
@RequestMapping("/api/private/v1/community/file")
public class FileController {

    @SuppressWarnings("all")
    @Autowired
    private FileServiceImpl fileService;

    @AuthAccess(desc = "上传文件")
    @PostMapping("/upload")
    @ResponseBody
    public ResultFul<?> upload(MultipartFile file) throws IOException {
        return fileService.upload(file);
    }

    ;

    @AuthAccess(desc = "删除文件")
    @DeleteMapping("/delete")
    @ResponseBody
    public ResultFul<?> delete(@RequestBody ImagePath imagePath) throws IOException {
        return fileService.delete(imagePath);
    }

    ;
}
