package cn.abyss4393.config.interceptor;

import cn.abyss4393.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className InterceptorConfig
 * @description TODO
 * @date 4/9/2023
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    String[] addPathPatterns = {
            "/api/private/v1/community/**"
    };
    String[] excludePathPatterns = {
            "/api/private/v1/community/server/**"
    };
    @Bean
    protected JwtInterceptor generatorInterceptorInstance() {
        return new JwtInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(generatorInterceptorInstance())
                .addPathPatterns(addPathPatterns)
                .excludePathPatterns(excludePathPatterns);
    }
}
