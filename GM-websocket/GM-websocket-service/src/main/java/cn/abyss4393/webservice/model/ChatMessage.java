package cn.abyss4393.webservice.model;

import cn.abyss4393.po.User;
import lombok.Getter;
import lombok.Setter;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ChatMessages
 * @description TODO
 * @date 8/10/2023
 */
@Setter
@Getter
public class ChatMessage {

    private String type;
    private Object content;
    private String timestamp;


}
