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

    private String title;

    private String contentDes;

    private String content;

    private String type;

    private Integer positivenessCount;

    private Integer passivenessCount;

    private Integer collectCount;

    private String postTime;

    private Integer approved;

    public static void initDefaultAttr(Article article) {
        article.setPassivenessCount(0);
        article.setPassivenessCount(0);
        article.setCollectCount(0);
        article.setApproved(0);

    }
}
