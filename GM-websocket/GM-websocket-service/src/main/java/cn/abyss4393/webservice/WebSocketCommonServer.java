package cn.abyss4393.webservice;

import cn.abyss4393.mapper.MessageMapper;
import cn.abyss4393.mapper.UserMapper;
import cn.abyss4393.po.Message;
import cn.abyss4393.po.User;
import cn.abyss4393.utils.imgbed.ImageBedUtils;
import cn.abyss4393.utils.redis.RedisUtils;
import cn.abyss4393.utils.timestamp.TimeStampUtil;
import cn.abyss4393.vo.SimpleUserInfo;
import cn.abyss4393.webservice.encoder.WebSocketCustomEncoding;
import cn.abyss4393.webservice.handler.WebSocketHandler;
import cn.abyss4393.webservice.handler.WebSocketHandlerBehavior;
import cn.abyss4393.webservice.states.WebSocketStates;
import cn.abyss4393.webservice.utils.WebSocketMessageConverters;
import cn.abyss4393.webservice.utils.WebSocketMessageDecoder;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className WebSocketServer
 * @description TODO
 * @completion false
 * @date 4/9/2023
 */

@ServerEndpoint(value = "/server/common_chat/{sender_id}/{group_id}",
        encoders = WebSocketCustomEncoding.class)
@Component
public class WebSocketCommonServer extends WebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(WebSocketCommonServer.class);

    private static final String IN_SHORT = "富强、民主、文明、和谐，倡导自由、平等、公正、法治，倡导爱国、敬业、诚信、友善";
    private static final String TYPE = "group";

    private static UserMapper userMapper;
    private static MessageMapper messageMapper;

    private static RedisUtils redisUtils;

    /**
     * 注入spring容器的userMapper bean
     *
     * @param userMapper 用户数据库操作类
     */
    @Autowired(required = false)
    private void setUserMapper(UserMapper userMapper) {
        WebSocketCommonServer.userMapper = userMapper;
    }

    /**
     * 注入spring容器的messageMapper bean
     *
     * @param messageMapper 消息数据库操作类
     */
    @Autowired(required = false)
    private void setMessageMapper(MessageMapper messageMapper) {
        WebSocketCommonServer.messageMapper = messageMapper;
    }


    /**
     * 注入spring容器的messageMapper bean
     *
     * @param redisUtils redis模版类
     */
    @Autowired(required = false)
    private void setRedisUtils(RedisUtils redisUtils) {
        WebSocketCommonServer.redisUtils = redisUtils;
    }

    /**
     * 记录当前连接数
     */
    private static final Map<Integer, Session> sessionMaps = new ConcurrentHashMap<>();

    /**
     * 记录连接群用户的数据
     */
    private static final Map<Integer, SimpleUserInfo> userMaps = new HashMap<>();

    /**
     * 记录用群消息历史记录
     */
    private static final Map<String, Object> historyMaps = new HashMap<>();

    private static final Map<String, HashMap<String, String>> fileMaps = new HashMap<>() {{
        this.put("image", new HashMap<>());
        this.put("audio", new HashMap<>());
        this.put("video", new HashMap<>());
        this.put("file", new HashMap<>());
    }};

    private static final JSONArray historyArray = new JSONArray();

    private static final int MAX_MESSAGE_SIZE = 100;
    /**
     * 群id
     */
    private static Integer groupId;
    private Integer senderId;
    private String sysCurrentTime;

    private long sysCurrentTimeMillis;

    @OnOpen
    public void OnOpen(Session session, @NonNull @PathParam("sender_id") Integer id, @NonNull @PathParam("group_id") Integer gid) throws IOException {
        senderId = id;
        groupId = gid;
        sysCurrentTime = TimeStampUtil.getIntactTimestamp();
        sysCurrentTimeMillis = System.currentTimeMillis();
        System.currentTimeMillis();
        sessionMaps.put(id, session);
        User user = userMapper.getSimpleUserInfo(id);
        log.info("有新用户加入,nickname：{},当前在线人数：{}", user.getNickname(), sessionMaps.size());
        userMaps.put(id, new SimpleUserInfo(id, user.getNickname(), user.getAvatar()));
        session.getBasicRemote().sendText(WebSocketStates.WAITING.states);
    }


    @OnMessage(maxMessageSize = 22428800)
    public void OnMessage(Session session, @NonNull String message) throws IOException {
        if (WebSocketStates.WAITING.states.equalsIgnoreCase(message)) {
            session.getBasicRemote().sendText(WebSocketStates.WAITING.states);
            return;
        }
        log.info("服务端接成功接收到用户nickname={}的消息", userMaps.get(senderId).getNickname());
        JSONObject result = JSONUtil.parseObj(message);
        result.set("timestamp", sysCurrentTime);
        this.daemonHandler((String) result.get("type"), result);
        log.info("服务端正在广播消息~~~~");
    }


    @OnClose
    public void OnClose() {
        sessionMaps.remove(senderId);
        log.info("当前有一个连接关闭，移除nickname={}的用户，当前人数：{}",
                userMaps.get(senderId).getNickname(), sessionMaps.size());
//        historyMaps.putIfAbsent(sysCurrentTime, historyArray);
//        historyMaps.replace(sysCurrentTime, historyArray);
//        String key = String.valueOf(groupId);
//        JSONObject temp = new JSONObject();
//        temp.set("content", historyMaps);
//        temp.set("size", historyArray.toString().getBytes().length);
//        this.putCacheForRedis(key, temp);
//        this.store();
//        log.info("websocket已关闭");
    }

    @OnError
    public void OnError(Throwable error) {
        log.error("服务端发生错误！");
        error.printStackTrace();
    }


    private void store() {
        boolean exist = messageMapper.exists(new LambdaQueryWrapper<>() {{
            this.eq(Message::getSenderId, senderId);
            this.eq(Message::getReceiverId, groupId);
        }});
        if (exist) {
            if (historyArray.size() > MAX_MESSAGE_SIZE) {
                historyArray.subList(MAX_MESSAGE_SIZE, historyArray.size()).clear();
            }
            int update = messageMapper.update(
                    new Message() {{
                        this.setSenderId(senderId);
                        this.setReceiverId(groupId);
                        this.setContent(JSONUtil.toJsonStr(historyArray));
                    }}, new LambdaQueryWrapper<>() {{
                        this.eq(Message::getSenderId, senderId);
                        this.eq(Message::getReceiverId, groupId);
                    }});
            if (0 == update)
                throw new RuntimeException("尝试为未存在的消息更新消息");
        } else {
            int insert = messageMapper.insert(
                    new Message(
                            messageMapper.getCount() + 1,
                            senderId,
                            groupId,
                            JSONUtil.toJsonStr(historyArray),
                            TimeStampUtil.getTimestamp()));
            if (0 == insert)
                throw new RuntimeException("增加的消息已存在，请更新");
        }

    }

    private <T> void broadcastMessage(T message, Session toSession) {
        try {
            log.info("服务端发送给客户端[{}]发送消息", toSession.getId());
            if (message instanceof String) {
                toSession.getBasicRemote().sendText((String) message);
            } else {
                toSession.getBasicRemote().sendObject(message);
            }
        } catch (Exception e) {
            log.error("发送信息失败！", e);
        }
    }

    private void broadcastAllMessage(Object message) {
        sessionMaps.values().forEach((Session session) -> {
            log.info("服务端发送给客户端[{}]发送消息：{}", session.getId(), message);
            try {
                session.getBasicRemote().sendObject(message);
            } catch (Exception e) {
                log.error("服务端发送消息失败！", e);
            }
        });
    }

    private void putCacheForRedis(String key, JSONObject data) {
        long interval = (System.currentTimeMillis() - sysCurrentTimeMillis) / 1000;
        if (interval > 60 * 5L) {
            redisUtils.lSet(key, data);
            redisUtils.expire(key, 60 * 24 * 24 * 3);
        }
    }

    private void daemonHandler(@NonNull String type, @NonNull JSONObject json) {
        switch (type) {
            case "text":
                this.handlerText(() -> {
                    Map<String, Object> messageMaps = getMessageMaps(
                            senderId,
                            userMaps.get(senderId),
                            groupId,
                            "null",
                            json,
                            "text",
                            "消息类型为文本消息"
                    );
                    this.broadcastAllMessage(messageMaps);
                    historyArray.add(messageMaps);
                });
                break;
            case "image":
                this.handlerImage(() -> {
                    String originName = json.getJSONObject("content").getStr("name");
                    if (!fileMaps.get("image").containsKey(originName)) {
                        String image = json.getJSONObject("content").getStr("image");
                        String header = "data:image/";
                        int pos = image.indexOf(";");
                        String pure = image.substring(image.indexOf(",") + 1);
                        byte[] imageBytes = WebSocketMessageDecoder.decoder(Objects.requireNonNull(pure));
                        JSONObject uploadResult = JSONUtil.parseObj(ImageBedUtils.uploadFile(ImageBedUtils.IMAGE_PATH, originName, imageBytes));
                        String downloadURL = uploadResult.getJSONObject("content").getStr("download_url");
                        json.getJSONObject("content").replace("image", downloadURL);
                        fileMaps.get("image").put(originName, downloadURL);
                    }
                    json.getJSONObject("content").replace("image", fileMaps.get("image").get(originName));
                    Map<String, Object> messageMaps = getMessageMaps(
                            senderId,
                            userMaps.get(senderId),
                            groupId,
                            "null",
                            json,
                            "image",
                            "消息类型为图片消息"
                    );
                    this.broadcastAllMessage(messageMaps);
                    historyArray.add(messageMaps);
                });
                break;
            case "audio":
                this.handlerAudio(() -> {
//                    String originName = json.getJSONObject("content").getStr("name");
//                    if (!fileMaps.get("audio").containsKey(originName)) {
//                        String audio = json.getJSONObject("content").getStr("audio");
//                        byte[] audioBytes = WebSocketMessageDecoder.decoder(Objects.requireNonNull(audio));
//                        JSONObject uploadResult = JSONUtil.parseObj(ImageBedUtils.uploadFile(ImageBedUtils.AUDIO_PATH, originName, audioBytes));
//                        String downloadURL = uploadResult.getJSONObject("content").getStr("download_url");
//                        json.getJSONObject("content").replace("audio", downloadURL);
//                        fileMaps.get("audio").put(originName, downloadURL);
//                    }
//                    int audioDuration = AudioUtils.getAudioDuration("https://abyss4393.cn/audio/v1/hjzf.mp3");
//                    System.out.println(audioDuration + "s");
//                    json.getJSONObject("content").set("duration", audioDuration);
                    json.getJSONObject("content").replace("audio", "https://abyss4393.cn/audio/v1/hjzf.mp3");
                    Map<String, Object> messageMaps = getMessageMaps(
                            senderId,
                            userMaps.get(senderId),
                            groupId,
                            "null",
                            json,
                            "audio",
                            "消息类型为音频"
                    );
                    this.broadcastAllMessage(messageMaps);
                });
                break;
            case "video":
                this.handlerVideo(() -> {

                });
            case "file":
                this.handlerFile(() -> {

                });
            default:
                break;
        }
    }

    private static Map<String, Object> getMessageMaps(Integer senderId, Object senderData, Integer groupId, Object groupData, Object payload, String title, Object body) {
        return WebSocketMessageConverters.getMessageMap(
                senderId,
                senderData,
                groupId,
                groupData,
                TYPE,
                IN_SHORT,
                payload,
                title,
                body);
    }

    @Override
    protected void handlerText(WebSocketHandlerBehavior handlerBehavior) {
        handlerBehavior.handler();
    }

    @Override
    protected void handlerImage(WebSocketHandlerBehavior handlerBehavior) {
        handlerBehavior.handler();
    }

    @Override
    protected void handlerAudio(WebSocketHandlerBehavior handlerBehavior) {
        handlerBehavior.handler();
    }

    @Override
    protected void handlerVideo(WebSocketHandlerBehavior handlerBehavior) {
        handlerBehavior.handler();
    }

    @Override
    protected void handlerFile(WebSocketHandlerBehavior handlerBehavior) {
        handlerBehavior.handler();
    }

}
