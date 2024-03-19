package cn.abyss4393.utils.algorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className SimMatrix
 * @description TODO
 * @date 2024/3/8
 * @completion false
 */
public class SimMatrix {

    public static Map<Integer, Map<Integer, Double>> getSimMatrix(Map<Integer, Map<Integer, Integer>> CFMatrix, Map<Integer, Integer> num) {
        Map<Integer, Map<Integer, Double>> sim = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : CFMatrix.entrySet()) {
            Integer u = entry.getKey();
            Map<Integer, Integer> otherUser = entry.getValue();
            for (Map.Entry<Integer, Integer> userScore : otherUser.entrySet()) {
                Integer v = userScore.getKey();
                if (!sim.containsKey(u)) {
                    sim.put(u, new HashMap<>());
                }
                if (0 == num.get(u) || 0 == num.get(v)) sim.get(u).put(v, 0.0);
                else sim.get(u).put(v, CFMatrix.get(u).get(v) / Math.sqrt(num.get(u) * num.get(v)));
            }
        }
        return sim;
    }
}
