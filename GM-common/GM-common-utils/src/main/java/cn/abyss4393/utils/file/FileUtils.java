package cn.abyss4393.utils.file;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className FileUtils
 * @description TODO
 * @date 5/9/2023
 */
public class FileUtils {

    public static String getFileSuffix(String fileName) {
        return fileName.contains(".") ? fileName.substring(fileName.indexOf(".")) : null;
    }
}
