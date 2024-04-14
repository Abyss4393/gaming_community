package cn.abyss4393.vo;

import cn.abyss4393.po.Article;
import cn.abyss4393.po.EntryRecommend;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className RecommendVo
 * @description TODO
 * @date 2024/3/25
 * @completion false
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecommendVo {
    private EntryRecommend recommend;
    private Article article;
}
