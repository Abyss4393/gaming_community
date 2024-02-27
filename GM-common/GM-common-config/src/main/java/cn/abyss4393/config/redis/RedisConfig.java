package cn.abyss4393.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className RedisConfig
 * @description TODO
 * @date 3/9/2023
 */
@Configuration
public class RedisConfig {
    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {

        //为了开发方便，我们一般创建String类型的redis模板
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();

        //采用json序列化并对其作出配置
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_ARRAY);//该方法是指定序列化输入的类型，就是将数据库里的数据按照一定类型存储到redis缓存中。
        jackson2JsonRedisSerializer.setObjectMapper(om);//完成配置，放在jackson2JsonRedisSerializer序列化中

        //key采用redis的String的序列化方式
        template.setKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(jackson2JsonRedisSerializer);//value采用上面定义的json序列化方式
        template.setHashKeySerializer(jackson2JsonRedisSerializer);//hash采用上面定义的json序列化方式
        template.setHashValueSerializer(jackson2JsonRedisSerializer);//hash的value采用上面定义的json序列化方式

        // 连接redis数据库
        template.setConnectionFactory(redisConnectionFactory);
        template.afterPropertiesSet();


        return template;
    }
}

