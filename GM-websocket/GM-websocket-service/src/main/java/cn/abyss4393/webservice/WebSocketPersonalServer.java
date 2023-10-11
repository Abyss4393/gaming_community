package cn.abyss4393.webservice;

import cn.abyss4393.mapper.MessageMapper;
import cn.abyss4393.mapper.UserMapper;
import cn.abyss4393.po.Message;
import cn.abyss4393.po.User;
import cn.abyss4393.utils.redis.RedisUtils;
import cn.abyss4393.utils.timestamp.TimeStampUtil;
import cn.abyss4393.vo.SimpleUserInfo;
import cn.abyss4393.webservice.encoder.WebSocketCustomEncoding;
import cn.abyss4393.webservice.states.WebSocketStates;
import cn.abyss4393.webservice.utils.WebSocketMessageConverters;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className WebSocketPersonalServer
 * @description TODO
 * @date 4/9/2023
 */

@ServerEndpoint(value = "/server/personal_chat/{from_id}/{to_id}", encoders = WebSocketCustomEncoding.class)
@Component
public class WebSocketPersonalServer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketCommonServer.class);
    private static final String IN_SHORT = "信言不美，美言不信。善者不辩，辩者不善。知者不博，博者不知。";
    private static final String TYPE = "private";

    private static UserMapper userMapper;
    private static MessageMapper messageMapper;

    private static RedisUtils redisUtils;

    /**
     * 注入spring容器的userMapper bean
     *
     * @param userMapper 用户数据库操作类
     */
    @Autowired(required = false)
    public void setUserMapper(UserMapper userMapper) {
        WebSocketPersonalServer.userMapper = userMapper;
    }

    /**
     * 注入spring容器的messageMapper bean
     *
     * @param messageMapper 消息数据库操作类
     */
    @Autowired(required = false)
    public void setMessageMapper(MessageMapper messageMapper) {
        WebSocketPersonalServer.messageMapper = messageMapper;
    }

    /**
     * 注入redis模版类的bean
     *
     * @param redisUtils redis模版类
     */
    @Autowired(required = false)
    public void setRedisUtils(RedisUtils redisUtils) {
        WebSocketPersonalServer.redisUtils = redisUtils;
    }

    /**
     * 记录websocket连接的session
     */
    private static final Map<Integer, Session> sessionMaps = new ConcurrentHashMap<>();

    /**
     * 记录历史记录
     */
    private static final Map<String, Object> historyContainer = new HashMap<>();

    /**
     * 发送者id
     */
    private Integer senderId;
    private Integer receiverId;
    private String sysCurrentTime;
    private final List<SimpleUserInfo> userData = new ArrayList<>();

    private long beginTime;

    @OnOpen
    public void OnOpen(Session session, @PathParam("from_id") Integer fid, @PathParam("to_id") Integer tid) throws IOException, EncodeException {
        senderId = fid;
        receiverId = tid;
        sysCurrentTime = TimeStampUtil.getTimestamp();
        sessionMaps.put(fid, session);
        new ArrayList<>() {{
            this.add(fid);
            this.add(tid);
        }}.forEach(item -> {
            User tempInfo = userMapper.getSimpleUserInfo((Integer) item);
            userData.add(new SimpleUserInfo((Integer) item, tempInfo.getNickname(), tempInfo.getAvatar()));
        });
        beginTime = System.currentTimeMillis();
        Map<String, Object> messageMaps = getMessageMaps(
                fid,
                userData.get(0),
                tid,
                userData.get(1),
                null,
                "sys_info",
                "连接成功！");
        session.getBasicRemote().sendObject(messageMaps);
        log.info("websocket已连接(from:{}to:{})", fid, tid);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException, EncodeException {
        if (WebSocketStates.WAITING.states.equalsIgnoreCase(message))
            session.getBasicRemote().sendText(WebSocketStates.WAITING.states);
        JSONObject cmmData = JSONUtil.parseObj(message);
        String tempTimeStamp = TimeStampUtil.getIntactTimestamp();
        cmmData.set("timestamp", tempTimeStamp);
        Integer exitId = messageMapper.exitId(senderId, receiverId);
        if (StringUtils.checkValNull(exitId)) {
            int insert = messageMapper.insert(
                    new Message(
                            messageMapper.getCount() + 1,
                            senderId,
                            receiverId,
                            JSONUtil.toJsonStr(cmmData),
                            TimeStampUtil.getTimestamp()));
            if (insert == 0)
                throw new RuntimeException("增加的消息已存在，请更新");
        } else {
            int update = messageMapper.update(
                    new Message(JSONUtil.toJsonStr(cmmData), TimeStampUtil.getTimestamp()), new LambdaQueryWrapper<>() {{
                        this.eq(Message::getSenderId, senderId);
                        this.eq(Message::getReceiverId, receiverId);
                    }});
            if (0 == update)
                throw new RuntimeException("尝试为未存在的消息更新消息");
        }
        historyContainer.put(sysCurrentTime, cmmData);
        Map<String, Object> messageMaps = getMessageMaps(senderId, userData.get(0), receiverId, userData.get(1), cmmData, "聊天规则", "dawd");
        this.broadcastMessage(receiverId, messageMaps);
        log.info("接收到客户端的消息！");
    }


    @OnClose
    public void onClose() {
        sessionMaps.remove(senderId);
        int connectTime = (int) ((System.currentTimeMillis() - beginTime) / 1000);
        JSONObject finalObject = JSONUtil.parseObj(historyContainer.get(sysCurrentTime));
        finalObject.set("duration", connectTime + "s");
        finalObject.set("size", finalObject.toString().getBytes().length);
        historyContainer.replace(sysCurrentTime, finalObject);
        String key = String.valueOf(senderId);
        List<Object> result = redisUtils.lGet(key, 0, -1);
        assert result != null;
        if (StringUtils.checkValNull(result) && result.size() == 0) {
            redisUtils.lSet(key, new ArrayList<>() {{
                this.add(historyContainer);
            }});
        }
        result.add(historyContainer);
        redisUtils.lSet(key, result);
        redisUtils.expire(key, 60 * 24 * 24 * 3);
        log.info("websocket已关闭");
    }

    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
        log.error("websocket发生错误！");
    }

    public static Map<String, Object> getMessageMaps(Integer senderId, Object senderData, Integer receiveId, Object receiverData, Object data, String title, Object body) {
        return WebSocketMessageConverters.getMessageMap(
                senderId,
                senderData,
                receiveId,
                receiverData,
                TYPE,
                IN_SHORT,
                data,
                title,
                body);

    }

    public void broadcastMessage(Integer tid, Object messages) {
        try {
            sessionMaps.get(tid).getBasicRemote().sendObject(messages);
        } catch (EncodeException | IOException e) {
            e.printStackTrace();
        }
    }
}
