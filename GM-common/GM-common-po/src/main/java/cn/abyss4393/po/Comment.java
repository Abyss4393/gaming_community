package cn.abyss4393.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className Comment
 * @description TODO
 * @date 19/11/2023
 * @completion false
 */
@TableName("tb_comment")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Comment implements Serializable {
    private Integer id;

    private Integer aId;

    private Integer uId;

    private String content;

    private Integer approved;

    private Integer commentLike;

    private Integer commentDislike;

    private String commentTime;

    public enum OrderBy {
        HOT(1),
        NEW(2),
        LATER(3);

        final int orderType;

        OrderBy(int orderType) {
            this.orderType = orderType;
        }
        public int getOrderType(){
            return this.orderType;
        }
    }

    public static void init(Comment comment) {
        comment.setApproved(0);
        comment.setCommentLike(0);
        comment.setCommentDislike(0);
    }

    @TableField(exist = false)
    private String nickname;

    @TableField(exist = false)
    private String avatar;


}
