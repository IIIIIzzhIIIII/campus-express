package com.campus.express.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / OpenAPI 接口文档配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("校园快递驿站管理系统 API")
                .description("处理快件入库、取件通知、取件登记、快件查询、问题件管理等")
                .version("1.0.0")
                .contact(new Contact().name("校园驿站")));
    }
}
