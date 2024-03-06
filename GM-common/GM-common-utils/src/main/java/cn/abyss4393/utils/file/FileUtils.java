package cn.abyss4393.utils.file;

import cn.abyss4393.utils.imgbed.ImageBedUtils;
import cn.abyss4393.utils.timestamp.TimeStampUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className FileUtils
 * @description TODO
 * @date 5/9/2023
 */
public class FileUtils {

    private static final Pattern IMG_PATTERN = Pattern.compile("<img.*src\\s*=\\s*(.*?)[^>]*?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern TEXT_PATTERN = Pattern.compile("<.?>|</.+?>", Pattern.CASE_INSENSITIVE);
    private static final Pattern SRC_PATTERN = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>\\s+)>", Pattern.CASE_INSENSITIVE);

    public static String getFileSuffix(String fileName) {
        return fileName.contains(".") ? fileName.substring(fileName.indexOf(".")) : null;
    }

    public static String replace(String content, List<String> targetList) {
        Element doc = Jsoup.parseBodyFragment(content).body();
        Elements image = doc.select("img[src]");
        for (int i = 0; i < targetList.size(); i++) {
            String src = image.get(i).attr("src");
            if (src.trim().startsWith("data:image/")) {
                src = targetList.get(i);
                image.get(i).attr("src", src);
            }
        }
        return doc.getElementsByTag("p").toString();
    }

    public static List<String> handlerBase64Content(String origenSrc) {
        Map<String, List<?>> imageMaps = generateImageMaps(origenSrc);
        List<?> fileNameList = imageMaps.get("fileNameList");
        List<?> bytesList = imageMaps.get("bytesList");
        List<String> remoteImageList = new ArrayList<>();
        for (int i = 0; i < fileNameList.size(); i++) {
            String filename = (String) fileNameList.get(i);
            byte[] bytes = (byte[]) bytesList.get(i);
            String response = ImageBedUtils.uploadFile(ImageBedUtils.EMOJI_IMAGE_PATH, filename, bytes);
            JSONObject jsonObject = JSONUtil.parseObj(response);
            if (StringUtils.checkValNull(jsonObject) || jsonObject.get(ImageBedUtils.CONSTANT.RESULT_BODY_COMMIT) == null)
                return null;
            remoteImageList.add(jsonObject.getJSONObject("content").getStr("download_url"));
        }
        return remoteImageList;
    }


    private static Map<String, List<?>> generateImageMaps(String origenSrc) {
        Map<String, List<?>> base64Map = new HashMap<>();
        List<String> fileNameList = new ArrayList<>();
        List<byte[]> bytesList = new ArrayList<>();
        if (StringUtils.isNotEmpty(origenSrc)) {
            List<String> imageOrigenSrc = getImageOrigenSrc(origenSrc);
            for (String image : imageOrigenSrc) {
                String imageType = image.substring(image.indexOf("image/") + 6, image.indexOf(";"));
                String fileName = TimeStampUtil.getTimestamp().substring(0, 13).replace("-", "").replace(" ", "") + "." + imageType;
                String base64Image = image.substring(image.indexOf("base64,") + 7);
                Base64.Decoder decoder = Base64.getDecoder();
                byte[] bytes = decoder.decode(base64Image);
                fileNameList.add(fileName);
                bytesList.add(bytes);
            }
            base64Map.put("fileNameList", fileNameList);
            base64Map.put("bytesList", bytesList);
        }
        return base64Map;
    }

    /**
     * 获取图富文本的文本数据
     *
     * @param richText String
     * @return String
     */
    public static String getText(String richText) {
        Matcher textMatcher = TEXT_PATTERN.matcher(richText);
        while (textMatcher.find()) {
            richText = richText.replace(" ", "");
        }
        return richText;
    }

    /**
     * 获取图富文本的图片数据
     *
     * @param longStrText String
     * @return List<?>
     */
    public static List<String> getImageOrigenSrc(String longStrText) {
        List<String> imageList = new ArrayList<>();
        Matcher imageMatcher = IMG_PATTERN.matcher(longStrText);
        if (imageMatcher.find()) {
            String img = imageMatcher.group();
            Matcher srcMatcher = SRC_PATTERN.matcher(img);
            while (srcMatcher.find()) {
                imageList.add(srcMatcher.group(1));
            }
        }
        return imageList;
    }


}
