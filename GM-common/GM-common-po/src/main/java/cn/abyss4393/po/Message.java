package cn.abyss4393.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className Message
 * @description TODO
 * @date 5/9/2023
 */
@TableName("tb_message")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message {
    private Integer id;
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private String createTime;


    public Message(String content, String createTime) {
        this.content = content;
        this.createTime = createTime;
    }

}
