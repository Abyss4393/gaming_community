package cn.abyss4393.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ManagerNotification
 * @description TODO
 * @date 2024/3/19
 * @completion false
 */

@TableName("tb_manager_notification")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ManagerNotification {
    public enum NOTIFICATION_TYPE {
        ARTICLE,
        COMMENT,
        REPLY
    }

    private Integer id;
    private String info;
    private String type;
    private String operationTime;
    private Integer isRead;
}
