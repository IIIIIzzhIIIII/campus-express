package com.campus.express.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 单机版（H2）数据库初始化。
 *
 * <p>只有在数据库文件为空（不存在 sys_user 表）时才导入建表脚本与演示数据，
 * 绝不会覆盖用户已经录入的数据。数据文件默认位于
 * {@code %USERPROFILE%\.campus-express\data}。</p>
 */
@Slf4j
@Component
@Profile("standalone")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StandaloneDataInitializer implements ApplicationRunner {

    private static final String SCHEMA_SCRIPT = "db/h2/schema-h2.sql";
    private static final String DATA_SCRIPT = "db/h2/data-h2.sql";

    private final DataSource dataSource;

    public StandaloneDataInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        try (Connection connection = dataSource.getConnection()) {
            if (tableExists(connection, "sys_user")) {
                log.info("【单机版】已检测到现有数据库，跳过初始化。数据目录：{}",
                        Paths.get(System.getProperty("user.home"), ".campus-express", "data"));
                return;
            }
            log.info("【单机版】首次启动，正在初始化 H2 数据库 ...");
            execute(connection, SCHEMA_SCRIPT);
            execute(connection, DATA_SCRIPT);
            log.info("【单机版】数据库初始化完成，演示账号：admin/123456、stu001/123456");
        } catch (Exception e) {
            throw new IllegalStateException("【单机版】数据库初始化失败：" + e.getMessage(), e);
        }
    }

    private boolean tableExists(Connection connection, String table) {
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT 1 FROM `" + table + "` LIMIT 1")) {
            return true;
        } catch (Exception e) {
            // 表不存在时会抛出异常，说明是全新的数据库
            return false;
        }
    }

    private void execute(Connection connection, String classpathScript) {
        ClassPathResource resource = new ClassPathResource(classpathScript);
        if (!resource.exists()) {
            throw new IllegalStateException("找不到初始化脚本：" + classpathScript);
        }
        ScriptUtils.executeSqlScript(connection,
                new EncodedResource(resource, StandardCharsets.UTF_8));
        log.info("【单机版】已执行 {}", classpathScript);
    }
}
