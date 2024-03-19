package cn.abyss4393.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className Reply
 * @description TODO
 * @date 26/11/2023
 * @completion false
 */
@TableName("tb_reply")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Reply {

    private Integer id;

    private String replier;

    private Integer articleId;

    private Integer userId;

    private Integer parentCommentId;

    private Integer parentReplyId;

    private String commenter;

    private String content;

    private Integer replyLike;

    private Integer replyDislike;

    private Integer approved;

    private String replyTime;

    public static void init(Reply reply) {
        reply.setApproved(0);
        reply.setReplyLike(0);
        reply.setReplyDislike(0);
    }

}
