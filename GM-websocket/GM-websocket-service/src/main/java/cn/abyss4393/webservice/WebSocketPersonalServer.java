package cn.abyss4393.webservice;

import cn.abyss4393.mapper.MessageMapper;
import cn.abyss4393.mapper.UserMapper;
import cn.abyss4393.po.Message;
import cn.abyss4393.po.User;
import cn.abyss4393.utils.imgbed.ImageBedUtils;
import cn.abyss4393.utils.redis.RedisUtils;
import cn.abyss4393.utils.timestamp.TimeStampUtil;
import cn.abyss4393.webservice.encoder.WebSocketCustomEncoding;
import cn.abyss4393.webservice.handler.WebSocketHandler;
import cn.abyss4393.webservice.handler.WebSocketHandlerBehavior;
import cn.abyss4393.webservice.model.ChatMessage;
import cn.abyss4393.webservice.model.ComplexChatMessage;
import cn.abyss4393.webservice.states.WebSocketStates;
import cn.abyss4393.webservice.utils.WebSocketMessageDecoder;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className WebSocketPersonalServer
 * @description TODO
 * @date 4/9/2023
 */

@ServerEndpoint(value = "/server/personal/{sender_id}/{receiver_id}", encoders = WebSocketCustomEncoding.class)
@Component
public class WebSocketPersonalServer extends WebSocketHandler {
    private static UserMapper userMapper;
    private static MessageMapper messageMapper;
    private static RedisUtils redisUtils;

    /**
     * 注入spring容器的userMapper bean
     * @param userMapper 用户数据库操作类
     */
    @Autowired(required = false)
    public void setUserMapper(UserMapper userMapper) {
        WebSocketPersonalServer.userMapper = userMapper;
    }

    /**
     * 注入spring容器的messageMapper bean
     * @param messageMapper 消息数据库操作类
     */
    @Autowired(required = false)
    public void setMessageMapper(MessageMapper messageMapper) {
        WebSocketPersonalServer.messageMapper = messageMapper;
    }

    /**
     * 注入redis模版类的bean
     * @param redisUtils redis模版类
     */
    @Autowired(required = false)
    public void setRedisUtils(RedisUtils redisUtils) {
        WebSocketPersonalServer.redisUtils = redisUtils;
    }

    private static final Logger log = LoggerFactory.getLogger(WebSocketCommonServer.class);
    private static final CopyOnWriteArraySet<WebSocketPersonalServer> webSocketSet = new CopyOnWriteArraySet<>();
    private static final Map<Integer, Session> sessionMaps = new ConcurrentHashMap<>();
    private static final Map<Integer, Object> userMaps = new ConcurrentHashMap<>();
    private static final Map<String, Set<String>> fileMaps = new ConcurrentHashMap<>();
    private static final Map<String, String> filePathMaps = new ConcurrentHashMap<>();
    private static final String IN_SHORT = "信言不美，美言不信。善者不辩，辩者不善。知者不博，博者不知";
    /**
     * 记录历史记录
     */
    private static final Map<String, List<ComplexChatMessage>> historyContainer = new ConcurrentHashMap<>();
    /**
     * 发送者id
     */
    private Integer senderId;
    /**
     * 接收者id
     */
    private Integer receiverId;

    private static final String MESSAGE_TYPE_TEXT = "text";
    private static final String MESSAGE_TYPE_IMAGE = "image";
    private static final String MESSAGE_TYPE_AUDIO = "audio";
    private static final String MESSAGE_TYPE_VIDEO = "video";
    private static final String MESSAGE_TYPE_FILE = "file";
    private long systemTime;

    static {
        Set<String> fileSet = new HashSet<>();
        fileMaps.put("image", fileSet);
        fileMaps.put("audio", fileSet);
        fileMaps.put("video", fileSet);
        fileMaps.put("file", fileSet);
        log.info("WebSocketServer init");
    }

    @OnOpen
    public void OnOpen(Session session, @PathParam("sender_id") Integer senderId,
                       @PathParam("receiver_id") Integer receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        systemTime = System.currentTimeMillis();
        User senderInfo = userMapper.getSimpleUserInfo(senderId);
        User receiverInfo = userMapper.getSimpleUserInfo(receiverId);
        userMaps.put(senderId, senderInfo);
        userMaps.put(receiverId, receiverInfo);
        sessionMaps.put(senderId, session);
        String historyKey = "senderId:" + senderId + "receiverId:" + receiverId;
        if (!historyContainer.containsKey(historyKey)) {
            List<ComplexChatMessage> history = new ArrayList<>();
            historyContainer.put(historyKey, history);
        }
        webSocketSet.add(this);
        log.info("websocket has been connected !\t发送id:{}\t接收id:{})", senderId, receiverId);
    }

    @OnMessage(maxMessageSize = 22428800)
    public void onMessage(Session session, @NonNull String message) throws IOException, EncodeException {
        if (WebSocketStates.WAITING.states.equalsIgnoreCase(message)) {
            session.getAsyncRemote().sendText(WebSocketStates.WAITING.states);
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
        chatMessage.setTimestamp(TimeStampUtil.getTimestamp());
        String type = chatMessage.getType();
        daemonHandler(type, chatMessage);
        store();
        log.info("服务端正在广播消息。。。");
    }

    @OnClose
    public void onClose() {
        sessionMaps.remove(senderId);

        webSocketSet.remove(this);
        log.info("websocket已关闭");
    }

    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
        log.error("websocket发生错误！");
    }

    private void daemonHandler(@NonNull String type, @NonNull ChatMessage chatMessage) throws JsonProcessingException {
        switch (type) {
            case MESSAGE_TYPE_TEXT -> handlerText(() -> {
                Map<String, Object> textMsgMaps = createMessageBody(chatMessage, type, "这是一个文本消息");
                sendMessage(textMsgMaps);
                cache(textMsgMaps);
            });
            case MESSAGE_TYPE_IMAGE -> handlerImage(() -> {
                JSONObject content = JSONUtil.parseObj(chatMessage.getContent());
                String originName = content.getStr("name");
                if (!fileMaps.get("image").contains(originName)) {
                    String image = content.getStr("image");
                    String base64Image = image.substring(image.indexOf("base64,") + 7);
                    byte[] imageBytes = WebSocketMessageDecoder.decoder(Objects.requireNonNull(base64Image));
                    JSONObject uploadResult = JSONUtil.parseObj(ImageBedUtils.uploadFile(ImageBedUtils.IMAGE_PATH, originName, imageBytes));
                    String downloadURL = uploadResult.getJSONObject("content").getStr("download_url");
                    fileMaps.get("image").add(originName);
                    filePathMaps.put(originName, downloadURL);
                    content.replace("image", downloadURL);
                } else {
                    String url = filePathMaps.get(originName);
                    content.replace("image", url);
                }
                chatMessage.setContent(content);
                Map<String, Object> imageMsgMaps = createMessageBody(chatMessage, type, "这是一个图片消息");
                sendMessage(imageMsgMaps);
                cache(imageMsgMaps);
            });
            case MESSAGE_TYPE_AUDIO -> handlerAudio(() -> {
                Map<String, Object> audioMsgMaps = createMessageBody(chatMessage, type, "这是一个音频");
                sendMessage(audioMsgMaps);
                cache(audioMsgMaps);
            });

            case MESSAGE_TYPE_VIDEO -> handlerVideo(() -> {
                Map<String, Object> videoMsgMaps = createMessageBody(chatMessage, type, "这是一个视频");
                sendMessage(videoMsgMaps);
                cache(videoMsgMaps);
            });

            case MESSAGE_TYPE_FILE -> handlerFile(() -> {
                Map<String, Object> fileMsgMaps = createMessageBody(chatMessage, type, "这是一个视频");
                sendMessage(fileMsgMaps);
                cache(fileMsgMaps);
            });

        }

    }

    public void sendMessage(Object messages) {
        Session senderSession = sessionMaps.get(this.senderId);
        Session receiverSession = sessionMaps.get(this.receiverId);
        senderSession.getAsyncRemote().sendObject(messages);
        if (null != receiverSession)
            receiverSession.getAsyncRemote().sendObject(messages);
    }

    private void cache(Map<String, Object> historyMap) throws JsonProcessingException {
        String historyKey = "senderId:" + senderId + "receiverId:" + receiverId;
        ObjectMapper read = new ObjectMapper();
        ComplexChatMessage complexChatMessage = read.readValue(JSONUtil.toJsonStr(historyMap), ComplexChatMessage.class);
        historyContainer.get(historyKey).add(complexChatMessage);
    }

    /**
     * 存储消息
     */
    private void store() {
        Thread thread = new Thread(() -> {
            synchronized (this) {
                List<Object> messageList = new ArrayList<>();
                String key = "senderId:" + senderId + "receiverId:" + receiverId;
                Optional<List<?>> optionalChatMessages = Optional.ofNullable(redisUtils.lGet(key, 0, -1));
                optionalChatMessages.orElseGet(ArrayList::new);
                if (optionalChatMessages.isPresent()) {
                    JSONArray innerJSONArray = JSONUtil.parseArray(JSONUtil.toJsonStr(optionalChatMessages.get()));
                    if (0 < innerJSONArray.size()) {
                        messageList.addAll(innerJSONArray);
                    }
                }
                List<ComplexChatMessage> messages = historyContainer.get(key);
                messageList.addAll(messages);
                redisUtils.lSet(key, messageList);
                redisUtils.expire(key, 60 * 24 * 24);
                long interval = (System.currentTimeMillis() - systemTime) / 1000;
                if (60 * 5 < interval) {
                    LambdaQueryWrapper<Message> lambdaQueryWrapper = Wrappers.lambdaQuery();
                    lambdaQueryWrapper.eq(Message::getSenderId, senderId);
                    lambdaQueryWrapper.eq(Message::getReceiverId, receiverId);
                    boolean exists = messageMapper.exists(lambdaQueryWrapper);
                    Message message = new Message();
                    message.setSenderId(senderId);
                    message.setReceiverId(receiverId);
                    message.setContent(JSONUtil.toJsonStr(messageList));
                    message.setCreateTime(TimeStampUtil.getTimestamp());
                    if (exists) {
                        messageMapper.update(message, lambdaQueryWrapper);
                    } else {
                        message.setId(Math.toIntExact(messageMapper.selectCount(null) + 1));
                        messageMapper.insert(message);
                    }
                    // 删除historyMaps中的数据
                    historyContainer.get(key).clear();
                }
                long redisSize = redisUtils.lGetListSize(key);
                if (100L < redisSize) {
                    redisUtils.del(key);
                }
            }
        });
        thread.start();
    }

    /**
     * @param payload       消息内容
     * @param notifications 可变参数通知列表
     */
    private Map<String, Object> createMessageBody(Object payload, String... notifications) {
        Map<String, Object> map = new HashMap<>();
        map.put("timeMillis", System.currentTimeMillis());
        map.put("senderId", this.senderId);
        map.put("senderData", userMaps.get(this.senderId));
        map.put("receiverId", this.receiverId);
        map.put("receiverData", userMaps.get(this.receiverId));
        map.put("motto", IN_SHORT);
        map.put("payload", payload);
        map.put("recalled", false);
        map.put("notification", new HashMap<>() {{
            this.put("title", notifications[0]);
            this.put("body", notifications[1]);
        }});
        return map;
    }

    /**
     * 处理文本消息
     * @param handlerBehavior function handlerText
     * @throws JsonProcessingException 异常
     */
    @Override
    protected void handlerText(WebSocketHandlerBehavior handlerBehavior) throws JsonProcessingException {
        handlerBehavior.handler();
    }

    /**
     * 处理文本消息
     * @param handlerBehavior function handlerImag
     * @throws JsonProcessingException 异常
     */
    @Override
    protected void handlerImage(WebSocketHandlerBehavior handlerBehavior) throws JsonProcessingException {
        handlerBehavior.handler();
    }

    /**
     * 处理文本消息
     * @param handlerBehavior function handlerAudio
     * @throws JsonProcessingException 异常
     */
    @Override
    protected void handlerAudio(WebSocketHandlerBehavior handlerBehavior) throws JsonProcessingException {
        handlerBehavior.handler();
    }

    /**
     * 处理文本消息
     * @param handlerBehavior function handlerVideo
     * @throws JsonProcessingException 异常
     */
    @Override
    protected void handlerVideo(WebSocketHandlerBehavior handlerBehavior) throws JsonProcessingException {
        handlerBehavior.handler();
    }

    /**
     * 处理文本消息
     * @param handlerBehavior function handlerFile
     * @throws JsonProcessingException 异常
     */
    @Override
    protected void handlerFile(WebSocketHandlerBehavior handlerBehavior) throws JsonProcessingException {
        handlerBehavior.handler();
    }
}
