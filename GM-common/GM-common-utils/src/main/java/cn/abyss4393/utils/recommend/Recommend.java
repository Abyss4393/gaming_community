package cn.abyss4393.utils.recommend;

import cn.abyss4393.utils.algorithm.CFMatrix;
import cn.abyss4393.utils.algorithm.SimMatrix;
import cn.abyss4393.utils.algorithm.SimilarityAlgorithm;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className Recommend
 * @description TODO
 * @date 2024/3/12
 * @completion false
 */


@Component
public class    Recommend {

    @Resource
    private SimilarityAlgorithm similarityAlgorithm;
    Map<Integer, Set<Integer>> smiSetMaps;
    Map<Integer, Integer> simNumMaps;
    Map<Integer, Map<Integer, Integer>> cfMatrix;
    Map<Integer, Map<Integer, Double>> simMatrix;


    @Bean
    public Recommend getRecommend() {
        return new Recommend();
    }

    public void init() {
        similarityAlgorithm.initSimilarityMaps();
        smiSetMaps = similarityAlgorithm.getSmiSetMaps();
        simNumMaps = similarityAlgorithm.getSimNumMaps();

        Map<Integer, Set<Integer>> reverseMaps = similarityAlgorithm.getReverseMaps(smiSetMaps);
        cfMatrix = CFMatrix.getCFMatrix(reverseMaps);
        simMatrix = SimMatrix.getSimMatrix(cfMatrix, simNumMaps);
    }

    /**
     * @param K      相似用户个数
     * @param N      推荐个数
     * @param target 目标
     * @return Set<Integer>
     */
    public Set<Integer> toUser(int K, int N, int target) {
        Map<Integer, Double> rank = new HashMap<>();
        if (simMatrix.containsKey(target)) {
            Set<Integer> simUser = smiSetMaps.get(target);
            List<Map.Entry<Integer, Double>> sortSim = new ArrayList<>(simMatrix.get(target).entrySet());
            sortSim.sort((s1, s2) -> Double.compare(s2.getValue(), s1.getValue()));
            for (int i = 0; i < K; i++) {
                // 前k个相似度相同的用户
                if (i >= sortSim.size()) break;
                Integer simUserId = sortSim.get(i).getKey();
                double simScore = sortSim.get(i).getValue();
                for (int itemId : smiSetMaps.get(simUserId)) {
                    if (smiSetMaps.get(target).contains(itemId)) continue;
                    rank.put(itemId, rank.getOrDefault(itemId, 0.0 + simScore));
                }
            }
        }
        List<Map.Entry<Integer, Double>> top = new ArrayList<>(rank.entrySet());
        Set<Integer> recommend = new HashSet<>();
        top.sort((t1, t2) -> Double.compare(t2.getValue(), t1.getValue()));
        for (int i = 0; i < Math.min(N, top.size()); i++) {
            recommend.add(top.get(i).getKey());
        }
        return recommend;
    }
}
