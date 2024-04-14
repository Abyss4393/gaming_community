package cn.abyss4393.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className dawda
 * @description TODO
 * @date 2/9/2023
 */
@RestController
public class Generator {

    private static final String URL = "jdbc:mysql://localhost:3306/cms?allowPublicKeyRetrieval=true&useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai";

    /**
     * 代码生成器
     */

        public static void main(String[] args) {
            // 代码生成器
            AutoGenerator mpg = new AutoGenerator();

            // 全局配置
            GlobalConfig globalConfig = new GlobalConfig();
            globalConfig
                    .setAuthor("abyss4393@gmail.com")
                    .setOpen(false)
                    .setFileOverride(false)
                    .setIdType(IdType.AUTO)
                    .setBaseResultMap(true)
                    .setEntityName("%s")
                    .setServiceName("%sService");
            mpg.setGlobalConfig(globalConfig);

            // 数据源配置
            DataSourceConfig dataSourceConfig = new DataSourceConfig();
            dataSourceConfig
                    .setUrl(URL)
                    .setDriverName("com.mysql.cj.jdbc.Driver")
                    .setUsername("game_community")
                    .setPassword("6KDLnpsTj5NmDKaB");
            mpg.setDataSource(dataSourceConfig);

            // 包名配置
            PackageConfig packageConfig = new PackageConfig();
            packageConfig
                    .setParent("com.abyss4393.mybatisplus")
                    .setPathInfo(getPathInfo())
                    .setEntity("entity")
                    .setController("controller")
                    .setXml("xml");
            mpg.setPackageInfo(packageConfig);


            // 策略配置
            StrategyConfig strategyConfig = new StrategyConfig();
            strategyConfig
                    .setNaming(NamingStrategy.underline_to_camel)
                    .setSuperEntityClass("com.abyss4393.mybatisplus.entity.BaseEntity")
                    .setEntitySerialVersionUID(false)
                    .setEntityLombokModel(true)
                    .setRestControllerStyle(true)
                    .setSuperEntityColumns("id", "create_time")
                    .setInclude(scanner("表名，多个英文逗号分割").split(","))
                    .setControllerMappingHyphenStyle(true);
            mpg.setStrategy(strategyConfig);
            mpg.setTemplateEngine(new FreemarkerTemplateEngine());
            mpg.execute();
        }

        /**
         * 读取控制台内容
         */
        private static String scanner(String tip) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入" + tip + "：");
            if (scanner.hasNext()) {
                String ipt = scanner.next();
                if (StringUtils.isNotBlank(ipt)) {
                    return ipt;
                }
            }
            throw new MybatisPlusException("请输入正确的" + tip + "！");
        }

        private static Map<String, String> getPathInfo() {
            Map<String, String> pathInfo = new HashMap<>();
            pathInfo.put(ConstVal.ENTITY_PATH, System.getProperty("user.dir") + "/src/main/java/com/demo/mybatisplus/entity");
            pathInfo.put(ConstVal.MAPPER_PATH, System.getProperty("user.dir") + "/src/main/java/com/demo/mybatisplus/mapper");
            pathInfo.put(ConstVal.SERVICE_PATH, System.getProperty("user.dir") + "/src/main/java/com/demo/mybatisplus/service");
            pathInfo.put(ConstVal.SERVICE_IMPL_PATH, System.getProperty("user.dir") + "/src/main/java/com/demo/mybatisplus/service/impl");
            pathInfo.put(ConstVal.CONTROLLER_PATH, System.getProperty("user.dir") + "/src/main/java/com/demo/mybatisplus/controller");
            pathInfo.put(ConstVal.XML_PATH, System.getProperty("user.dir") + "/src/main/resources/mapper");
            return pathInfo;
        }
}
