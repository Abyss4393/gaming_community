package cn.abyss4393.webservice.model;

import cn.abyss4393.po.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ComplexChatMessage
 * @description TODO
 * @date 2024/3/14
 * @completion false
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ComplexChatMessage {

    private String timeMillis;
    private Integer senderId;
    private User senderData;
    private Integer receiverId;
    private User receiverData;
    private String motto;
    private Object payload;
    private boolean recalled;
    private Object notification;
}
