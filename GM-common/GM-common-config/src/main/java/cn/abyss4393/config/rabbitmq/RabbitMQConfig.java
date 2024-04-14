package cn.abyss4393.config.rabbitmq;

import cn.abyss4393.utils.rabbitmq.RabbitMQConstantUtils;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className RabbitMQConfig
 * @description TODO
 * @date 2024/3/2
 * @completion false
 */
@Configuration
@EnableRabbit
public class RabbitMQConfig {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        //SimpleRabbitListenerContainerFactory发现消息中有content_type有text就会默认将其转换成string类型的
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        /*
         * 比较常用的 Converter 就是 Jackson2JsonMessageConverter,在发送消息时，它会先将自定义的消息类序列化成json格式，
         * 再转成byte构造 Message，在接收消息时，会将接收到的 Message 再反序列化成自定义的类
         */
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //开启手动ACK
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }


    @Bean
    public AmqpTemplate amqpTemplate() {
        rabbitTemplate.setEncoding("UTF-8");
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        /*
         * ReturnsCallback消息没有正确到达队列时触发回调，如果正确到达队列不执行
         * config : 需要开启rabbitmq发送失败回退
         * yml配置publisher-returns: true
         * 或rabbitTemplate.setMandatory(true);设置为true
         */
        rabbitTemplate.setReturnsCallback(returnedMessage -> {
            String messageId = returnedMessage.getMessage().getMessageProperties().getMessageId();
            byte[] message = returnedMessage.getMessage().getBody();
            Integer replyCode = returnedMessage.getReplyCode();
            String replyText = returnedMessage.getReplyText();
            String exchange = returnedMessage.getExchange();
            String routingKey = returnedMessage.getRoutingKey();
            log.info("消息：{} 发送失败，消息ID：{} 应答码：{} 原因：{} 交换机：{} 路由键：{}",
                    new String(message), messageId, replyCode, replyText, exchange, routingKey);
        });
        return rabbitTemplate;
    }

    /**
     * 声明直连交换机  支持持久化
     *
     * @return Exchange
     */
    @Bean(RabbitMQConstantUtils.DIRECT_EXCHANGE)
    public Exchange directExchange() {
        return ExchangeBuilder.directExchange(RabbitMQConstantUtils.DIRECT_EXCHANGE).durable(true).build();
    }

    /**
     * 用户消息队列
     *
     * @return Queue
     */
    @Bean(RabbitMQConstantUtils.USER_QUEUE)
    public Queue userQueue() {
        return new Queue(RabbitMQConstantUtils.USER_QUEUE, true, false, true);
    }

    /**
     * 把用户消息队列绑定到交换机上
     */
    @Bean
    public Binding userQueueBinding(@Qualifier(RabbitMQConstantUtils.USER_QUEUE) Queue queue,
                                        @Qualifier(RabbitMQConstantUtils.DIRECT_EXCHANGE) Exchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(RabbitMQConstantUtils.USER_QUEUE).noargs();
    }

    /**
     * 管理员消息队列
     *
     * @return Queue
     */
    @Bean(RabbitMQConstantUtils.MANAGE_QUEUE)
    public Queue managerQueue() {
        return new Queue(RabbitMQConstantUtils.MANAGE_QUEUE, true, false, true);
    }

    /**
     * 消息队列绑定到交换机上
     */
    @Bean
    public Binding managerQueueBinding(@Qualifier(RabbitMQConstantUtils.MANAGE_QUEUE) Queue queue,
                                        @Qualifier(RabbitMQConstantUtils.DIRECT_EXCHANGE) Exchange directExchange) {

        return BindingBuilder.bind(queue).to(directExchange).with(RabbitMQConstantUtils.MANAGE_QUEUE).noargs();
    }

}
