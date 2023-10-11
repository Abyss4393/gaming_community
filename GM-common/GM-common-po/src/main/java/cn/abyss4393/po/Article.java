package cn.abyss4393.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.DelayQueue;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className Article
 * @description TODO
 * @date 5/9/2023
 */

@TableName("tb_article")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Article implements Serializable {
    private Integer id;
    private String posterName;
    private Integer posterId;
    private String content;
    private String type;
    private int positivenessCount;
    private int passivenessCount;
    private int collectCount;
    private String postTime;


    public static void initDefaultAttr(Article article) {
        article.setPassivenessCount(0);
        article.setPassivenessCount(0);
        article.setCollectCount(0);
    }

    /**
     * first params id of article
     * second params id of user
     */
    @TableField(exist = false)
    private static final Map<Integer, Deque<Integer>> recordQueue = new HashMap<>();

    public static void addArtKey(int aid) {
        recordQueue.put(aid, new ArrayDeque<>());
    }

    public static void addItemVal(int aid, int uid) {
        recordQueue.get(aid).add(uid);
    }

    public static void removeItemVal(int aid, int uid) {
        recordQueue.get(aid).remove(uid);
    }
}
