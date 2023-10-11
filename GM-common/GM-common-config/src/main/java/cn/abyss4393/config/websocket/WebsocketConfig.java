package cn.abyss4393.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className WebsocketConfig
 * @description TODO
 * @date 4/9/2023
 */
@Configuration
public class WebsocketConfig {
    /*
     * 注册一个ServletEndpointExporter,
     * 该Bean自动注册使用@ServletEndpoint注解声明中的websocket Endpoint
     */

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
