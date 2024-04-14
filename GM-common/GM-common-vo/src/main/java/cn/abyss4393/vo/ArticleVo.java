package cn.abyss4393.vo;

import cn.abyss4393.po.Article;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ArticleVo
 * @description TODO
 * @date 2024/3/5
 * @completion false
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ArticleVo {
    private Article article;
    private String notificationContent;
    private String avatar;
    private String nickname;

    private boolean isRecommend;

    public ArticleVo(Article article, String notificationContent, String avatar, String nickname) {
        this.article = article;
        this.notificationContent = notificationContent;
        this.avatar = avatar;
        this.nickname = nickname;
    }
}
