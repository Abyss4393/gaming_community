package cn.abyss4393.config.rabbitmq;

import cn.abyss4393.mapper.ManagerNotificationMapper;
import cn.abyss4393.mapper.UserNotificationMapper;
import cn.abyss4393.po.ManagerNotification;
import cn.abyss4393.po.UserNotification;
import cn.abyss4393.utils.rabbitmq.RabbitMQConstantUtils;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ReceiverForUser
 * @description TODO
 * @date 2024/3/3
 * @completion false
 */

//@ServerEndpoint(value = "/sys", encoders = WebSocketCustomEncoding.class)
@Component
public class RabbitMQListener {

    @Resource
    private UserNotificationMapper userNotificationMapper;

    @Resource
    private ManagerNotificationMapper managerNotificationMapper;
    private static final Logger log = LoggerFactory.getLogger(RabbitMQListener.class);

    @RabbitHandler
    @RabbitListener(queues = RabbitMQConstantUtils.USER_QUEUE)
    public void handlerUserQueue(Channel channel, @NonNull Message message) throws Exception {
        String msgBody = new String(message.getBody());
        JSONObject msg = JSONUtil.parseObj(msgBody);
        log.info("user_queue: 消费消息：{}", msg);
        Integer rowTotal = Math.toIntExact(userNotificationMapper.selectCount(null) + 1);
        Integer targetId = msg.getInt("target_user_id");
        String msgContent = msg.getStr("message_content");
        String type = msg.getStr("message_type");
        String operateTime = msg.getStr("operate_time");
        UserNotification userNotification = new UserNotification(rowTotal, targetId, msgContent, type, operateTime, 0);
        try {
            userNotificationMapper.insert(userNotification);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("消息处理出现异常：{}", e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    @RabbitHandler
    @RabbitListener(queues = RabbitMQConstantUtils.MANAGE_QUEUE)
    public void handlerManageQueue(Channel channel, Message message) throws Exception {
        String msgBody = new String(message.getBody());
        JSONObject msg = JSONUtil.parseObj(msgBody);
        Integer row = Math.toIntExact((managerNotificationMapper.selectCount(null) + 1));
        String msgContent = msg.getStr("message_content");
        String type = msg.getStr("message_type");
        String operateTime = msg.getStr("operate_time");
        ManagerNotification managerNotification = new ManagerNotification(row, msgContent, type, operateTime, 0);
        try {
            managerNotificationMapper.insert(managerNotification);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("消息处理出现异常：{}", e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}