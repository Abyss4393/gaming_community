package cn.abyss4393.vo;

import cn.abyss4393.po.Comment;
import cn.abyss4393.po.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className CommentVo
 * @description TODO
 * @date 20/11/2023
 * @completion false
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVo {
    private Comment comment;

    private User user;
    private String notificationContent;

    public CommentVo(Comment comment) {
        this.comment = comment;
    }

    public CommentVo(Comment comment, String notificationContent) {
        this.comment = comment;
        this.notificationContent = notificationContent;
    }
}
