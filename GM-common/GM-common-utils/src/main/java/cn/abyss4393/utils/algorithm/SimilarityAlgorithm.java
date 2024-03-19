package cn.abyss4393.utils.algorithm;

import cn.abyss4393.mapper.CollectionMapper;
import cn.abyss4393.mapper.UpvoteMapper;
import cn.abyss4393.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className SimilarityMaps
 * @description TODO
 * @date 2024/3/8
 * @completion false
 */


@Component
public class SimilarityAlgorithm {

    private final Map<Integer, Set<Integer>> smiSetMaps = new HashMap<>();
    private final Map<Integer, Integer> simNumMaps = new HashMap<>();

    @Resource
    private UserMapper userMapper;
    @Resource
    private UpvoteMapper upvoteMapper;
    @Resource
    private CollectionMapper collectionMapper;

    public void initSimilarityMaps() {
        // 初始化相似度映射
        List<Integer> allUserId = userMapper.getAllUserId();
        allUserId.forEach(userId -> {
            Set<Integer> articleSet = new HashSet<>();
            List<Integer> allArticleIdFromUpvote = upvoteMapper.getAllArticleId(userId);
            List<Integer> allArticleIdFromCollection = collectionMapper.getAllArticleId(userId);
            articleSet.addAll(allArticleIdFromUpvote);
            articleSet.addAll(allArticleIdFromCollection);
            smiSetMaps.put(userId, articleSet);
            simNumMaps.put(userId, articleSet.size());
        });
    }

    @Bean
    public SimilarityAlgorithm getSimilarityAlgorithm() {
        return new SimilarityAlgorithm();
    }


    public Map<Integer, Set<Integer>> getSmiSetMaps() {
        return smiSetMaps;
    }

    public Map<Integer, Integer> getSimNumMaps() {
        return simNumMaps;
    }

    public Map<Integer, Set<Integer>> getReverseMaps(Map<Integer, Set<Integer>> targetMaps) {
        Map<Integer, Set<Integer>> reverseMaps = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> entry : targetMaps.entrySet()) {
            Integer userId = entry.getKey();
            Set<Integer> value = entry.getValue();
            for (Integer articleId : value) {
                Set<Integer> userSet = targetMaps.getOrDefault(articleId, new HashSet<>());
                userSet.add(userId);
                reverseMaps.put(articleId, userSet);
            }
        }
        return reverseMaps;
    }
}
