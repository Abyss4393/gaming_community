package cn.abyss4393.utils.algorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className CFMatrix
 * @description TODO
 * @date 2024/3/8
 * @completion false
 */
public class CFMatrix {
    public static Map<Integer, Map<Integer, Integer>> getCFMatrix(Map<Integer, Set<Integer>> reverseMaps) {
        Map<Integer, Map<Integer, Integer>> CFMatrix = new HashMap<>();
        // 遍历所有的帖子，统计用户两两之间交互的帖子数
        for (Map.Entry<Integer, Set<Integer>> entry : reverseMaps.entrySet()) {
            Integer article = entry.getKey();
            Set<Integer> users = entry.getValue();
            // 首先统计每个用户交互的帖子个数
            for (Integer u : users) {
                //遍历所有该帖子对应的交互过的用户
                // 统计每个用户与其它用户共同交互的物品个数
                if (!CFMatrix.containsKey(u)) {
                    CFMatrix.put(u, new HashMap<>());
                }
                for (Integer v : users) {
                    //再次遍历所有用户，对不是u的其他用户进行操作
                    if (!v.equals(u)) {
                        if (!CFMatrix.get(u).containsKey(v)) {
                            CFMatrix.get(u).put(v, 0);
                        }
                        CFMatrix.get(u).put(v, CFMatrix.get(u).get(v) + 1);
                    }
                }
            }
        }
        return CFMatrix;
    }
}
