package cn.abyss4393.webservice.utils;

import cn.abyss4393.utils.timestamp.TimeStampUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className WebSocketMessageConverters
 * @description TODO
 * @date 8/10/2023
 */
public class WebSocketMessageConverters {

    private static final String VERSION = "1.0.SNAPSHOT";

    public static Map<String, Object> createMessageBody(Integer senderId, Object senderData,
                                                        Integer receiveId, Object receiverData,
                                                        String type, String important,
                                                        Object payloadData, String title,
                                                        Object body) {
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("proVersion", VERSION);
        messageMap.put("timeMillis", System.currentTimeMillis());
        messageMap.put("senderId", senderId);
        messageMap.put("senderData", senderData);
        messageMap.put("receiverId", receiveId);
        messageMap.put("to", new HashMap<>() {{
            this.put("type", type);
            this.put("data", receiverData);
        }});
        messageMap.put("important", important);
        messageMap.put("payload", payloadData);
        messageMap.put("recalled",false);
        messageMap.put("notification", new HashMap<>() {{
            this.put("title", title);
            this.put("body", body);
        }});
        return messageMap;
    }
}
