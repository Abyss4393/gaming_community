package cn.abyss4393.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className UserNotification
 * @description TODO
 * @date 2024/3/5
 * @completion false
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_user_notification")
public class UserNotification {

    public enum NOTIFICATION_TYPE {
        SYSTEM,
        COMMENT,
        REPLY
    }

    private Integer id;

    private Integer userId;

    private String info;

    private String type;

    private String operateTime;

    private Integer isRead;
}
