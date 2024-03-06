package cn.abyss4393.utils.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.HashMap;
import java.util.Map;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className PageUtils
 * @description TODO
 * @date 2024/3/2
 * @completion false
 */
public class PageUtils {
    public static Map<String, Object> createPageResultMap(Page<?> page) {
        Map<String, Object> container = new HashMap<>();
        container.put("data", page.getRecords());
        container.put("currentPage", page.getCurrent());
        container.put("pageSize", page.getSize());
        container.put("total", page.getTotal());
        return container;
    }
}
