package cn.abyss4393.utils.imgbed;

import cn.abyss4393.utils.file.FileUtils;
import cn.abyss4393.utils.timestamp.TimeStampUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;


import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ImageBedUtils
 * @description TODO
 * @date 5/9/2023
 */
public class ImageBedUtils {

    private static final String ACCESS_TOKEN = "a0c792eb0eb050683d31f615f4a69bca";

    private static final String NAME_SPACE = "abyss4393";

    private static final String REPOSITORY = "gaming_community";

    private static final String PATH = "test/";

    private static final int TIME_OUT = 10000;

    public static class CONSTANT {
        public static final String RESULT_BODY_COMMIT = "commit";
        public static final String RESULT_BODY_CONTENT = "content";
        public static final String RESULT_BODY_SHA = "sha";
        public static final String RESULT_BODY_DOWNLOAD_URL = "url";
    }


    private static final String API_BASIC_URL_POST = "https://gitee.com/api/v5/repos/%s/%s/contents/%s";

    private static String createUploadFileUrl(String originalFileName, String path) {
        String targetPath = path == null ? PATH : path;
        String suffix = FileUtils.getFileSuffix(originalFileName);
        String fileName = TimeStampUtil.getSingleTimeStamp().replace("-", "") + UUID.randomUUID().toString().replace("-", "") + suffix;
        return String.format(API_BASIC_URL_POST,
                NAME_SPACE,
                REPOSITORY,
                targetPath + fileName);
    }

    private static String creDelFileUrl(String filePath) {
        return String.format(API_BASIC_URL_POST,
                NAME_SPACE,
                REPOSITORY,
                filePath);
    }

    private static HashMap<String, Object> getRequestBodyMap(byte[] multipartFile) {
        return new HashMap<>() {{
            this.put("access_token", ACCESS_TOKEN);
            this.put("message", "add file!");
            this.put("content", Base64.getEncoder().encodeToString(multipartFile));
        }};
    }

    private static HashMap<String, Object> getCommonRequestBodyMap(HashMap<String, Object> hashMap, String msg) {
        HashMap<String, Object> newHm = new HashMap<>();
        if (hashMap != null)
            newHm.putAll(hashMap);
        newHm.put("access_token", ACCESS_TOKEN);
        newHm.put("message", msg);
        return newHm;
    }

    public static String uploadFile(String path, String originalFileName, byte[] data) {
        String url = createUploadFileUrl(originalFileName, path);
        HashMap<String, Object> file = ImageBedUtils.getRequestBodyMap(data);
        return HttpUtil.post(url, file);
    }

    public static String deleteFile(String filePath, String sha) {
        return HttpUtil.createRequest(Method.DELETE, creDelFileUrl(filePath))
                .form(getCommonRequestBodyMap(new HashMap<>() {{
                    this.put(CONSTANT.RESULT_BODY_SHA, sha);
                }}, "del file!"))
                .timeout(TIME_OUT).
                execute().
                body();
    }

    public static String getSha(String filePath) {
        String newFilePath = creDelFileUrl(filePath);
        return HttpUtil.createGet(newFilePath)
                .form(getCommonRequestBodyMap(null, "get sha!"))
                .timeout(TIME_OUT).execute().body();
    }
}
