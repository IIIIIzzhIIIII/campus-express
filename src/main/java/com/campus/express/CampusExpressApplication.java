package com.campus.express;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 校园快递驿站管理系统 启动类
 */
@SpringBootApplication
@MapperScan("com.campus.express.mapper")
@EnableScheduling
public class CampusExpressApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(CampusExpressApplication.class);
        // 单机版需要打开系统托盘 / 浏览器；服务器部署时该设置无副作用
        application.setHeadless(false);
        application.run(args);
    }
}
