package cn.abyss4393.utils.wrap;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className WrapUtils
 * @description TODO
 * @date 11/9/2023
 */
public class WrapUtils {

    /**
     * 去除对象的属性
     *
     * @param target 目标对象
     * @param attr   属性名
     * @param <T>    目标对象
     * @return 返回 所需对象
     */
    public static <T> Object removeAttr(T target, String attr) {
        final JSONObject jsonObject = JSONUtil.parseObj(target);
        jsonObject.remove(attr);
        return jsonObject;
    }


}
