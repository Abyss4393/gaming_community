package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.utils.imgbed.ImagePath;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IFileService {
    ResultFul<?> upload(MultipartFile multipartFile) throws IOException;

    ResultFul<?> uploadAvatar(MultipartFile multipartFile) throws IOException;
    ResultFul<?> delete(ImagePath imagePath);
}
