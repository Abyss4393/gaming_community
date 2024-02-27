package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.service.IFileService;
import cn.abyss4393.utils.ftp.FTPUtils;
import cn.abyss4393.utils.imgbed.ImageBedUtils;
import cn.abyss4393.utils.imgbed.ImagePath;
import cn.abyss4393.utils.timestamp.TimeStampUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className FileServiceImpl
 * @description TODO
 * @date 5/9/2023
 */
@Service
public class FileServiceImpl implements IFileService {
    @Override
    public ResultFul<?> upload(MultipartFile multipartFile) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename();
        if (StringUtils.checkValNull(originalFileName))
            return ResultFul.fail(BaseCode.IMAGE_URL_NULL);
        String ImageUploadResult = ImageBedUtils.uploadFile(ImageBedUtils.IMAGE_PATH, originalFileName, multipartFile.getBytes());
        JSONObject jsonObject = JSONUtil.parseObj(ImageUploadResult);
        if (StringUtils.checkValNull(jsonObject) || jsonObject.get(ImageBedUtils.CONSTANT.RESULT_BODY_COMMIT) == null)
            return ResultFul.fail(BaseCode.FILE_URL_ERROR);
        JSONObject responseObj = new JSONObject();
        int fileSize = jsonObject.getJSONObject("content").getInt("size") / 1024 ;
        String download = jsonObject.getJSONObject("content").getStr("download_url");
        responseObj.set("download", download);
        responseObj.set("size", fileSize);
        return ResultFul.success(BaseCode.FILE_UPLOAD_SUCCESS, new HashMap<>() {{
            this.put("content", responseObj);
        }});

    }

    @Override
    public ResultFul<?> ftpUpload(MultipartFile multipartFile) throws IOException {
        String fileName = TimeStampUtil.getSingleTimeStamp() + UUID.randomUUID() +
                Objects.requireNonNull(multipartFile.getOriginalFilename()).substring(multipartFile.getOriginalFilename().lastIndexOf("."));
        fileName = fileName.replace("\t", "").replace("-", "");
        boolean upload = FTPUtils.upload(fileName, multipartFile.getInputStream());

        String remoteUrl = "http://abyss4393.cn/ftp/" + fileName;

        return upload ? ResultFul.success(BaseCode.FILE_UPLOAD_SUCCESS, new HashMap<>() {{
                this.put("url", remoteUrl);
            }}) :
                ResultFul.fail(BaseCode.FILE_UPLOAD_ERROR);
    }

    @Override
    public ResultFul<?> uploadAvatar(MultipartFile multipartFile) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename();
        if (StringUtils.checkValNull(originalFileName))
            return ResultFul.fail(BaseCode.IMAGE_URL_NULL);
        String result = ImageBedUtils.uploadFile(ImageBedUtils.AVATAR_PATH, originalFileName, multipartFile.getBytes());
        JSONObject jsonObject = JSONUtil.parseObj(result);
        if (StringUtils.checkValNull(jsonObject) || jsonObject.get(ImageBedUtils.CONSTANT.RESULT_BODY_COMMIT) == null)
            return ResultFul.fail(BaseCode.FILE_URL_ERROR);
        return ResultFul.success(BaseCode.FILE_UPLOAD_SUCCESS, new HashMap<>() {{
            this.put("content", jsonObject.get("content"));
        }});
    }

    @Override
    public ResultFul<?> delete(ImagePath imagePath) {
        String imageUrl = imagePath.url();
        if (StringUtils.isBlank(imageUrl) || !imageUrl.contains("/master"))
            return ResultFul.fail(BaseCode.FILE_URL_ERROR);
        String pureUrl = imageUrl.substring(imageUrl.indexOf("master/") + 7);
        String sha = ImageBedUtils.getSha(pureUrl);
        JSONObject jsonObject = JSONUtil.parseObj(sha);
        String pureSha = jsonObject.getStr("sha");
        if (pureSha == null)
            ResultFul.error(BaseCode.IMAGE_NOT_PURE);
        String result = ImageBedUtils.deleteFile(pureUrl, pureSha);
        jsonObject = JSONUtil.parseObj(result);
        if (jsonObject.getObj(ImageBedUtils.CONSTANT.RESULT_BODY_CONTENT) == null)
            return ResultFul.fail(BaseCode.FILE_RESOURCE_NOTFOUND);
        return ResultFul.success(BaseCode.DELETE, new HashMap<>(1) {{
            this.put("file_name", pureUrl.substring(pureUrl.indexOf("/") + 1));
        }});
    }

}
