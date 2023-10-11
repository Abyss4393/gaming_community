package cn.abyss4393.webservice.model;

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
public class ChatMessages {

    private MessagesType type;
    private String content;
    private String sender;

    private enum MessagesType{
        CHAT,
        JOIN,
        LEAVE
    }
}
