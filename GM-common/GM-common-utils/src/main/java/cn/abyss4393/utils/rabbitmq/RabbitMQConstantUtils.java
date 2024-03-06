package cn.abyss4393.utils.rabbitmq;

import cn.abyss4393.utils.timestamp.TimeStampUtil;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Map;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className RabbitMQUtils
 * @description TODO
 * @date 2024/3/2
 * @completion false
 */

public class RabbitMQConstantUtils {


    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 交换机名称
     */
    public static final String DIRECT_EXCHANGE = "directExchange";

    /**
     * 用户消息队列
     */
    public static final String USER_QUEUE = "user-queue";

    /**
     * 管理员队列
     */
    public static final String MANAGE_QUEUE = "manage-queue";

    public static Map<String, Object> createMessageBodyForUser(Integer targetId, String msg) {
        String time = TimeStampUtil.getTimestamp();
        return Map.of("target_user_id", targetId,
                "message_content", msg, "operate_time", time);

    }

    public static Map<String, Object> createMessageBodyForManager(String msg) {
        String time = TimeStampUtil.getTimestamp();
        return Map.of("message_content", msg, "operate_time", time);
    }

}
