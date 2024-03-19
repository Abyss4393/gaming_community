package cn.abyss4393.vo;

import cn.abyss4393.po.Reply;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ReplyVo
 * @description TODO
 * @date 2024/3/7
 * @completion false
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReplyVo {
    private Reply reply;
    private String notificationContent;
}
