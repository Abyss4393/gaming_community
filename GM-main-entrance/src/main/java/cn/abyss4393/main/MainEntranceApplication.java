package cn.abyss4393.main;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className MainEntranceApplication
 * @description TODO
 * @date 3/9/2023
 */

@SpringBootApplication()
@ComponentScan(basePackages = {"cn.abyss4393.dto","cn.abyss4393.config"
        ,"cn.abyss4393.service","cn.abyss4393.utils","cn.abyss4393.webservice"})
@MapperScan(basePackages = {"cn.abyss4393.mapper"})
public class MainEntranceApplication {
    public static void main(String[] args) {
         SpringApplication.run(MainEntranceApplication.class, args);

    }
}
