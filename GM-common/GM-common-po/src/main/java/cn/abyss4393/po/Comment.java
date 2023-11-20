package cn.abyss4393.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

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
    private String replyTime;
}
