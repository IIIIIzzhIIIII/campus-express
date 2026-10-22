package com.campus.express.config;

import com.campus.express.util.DesktopUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 单机版启动完成后自动打开浏览器（campus.open-browser=true 时生效）。
 * 打不开浏览器（如服务器 / 无桌面环境）只记录日志，不影响程序运行。
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "campus.open-browser", havingValue = "true")
public class BrowserLauncher {

    @Value("${server.port:8081}")
    private int port;

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        String url = "http://localhost:" + port + "/login";
        new Thread(() -> {
            try {
                Thread.sleep(1200);
                if (DesktopUtils.openUrl(url)) {
                    log.info("【单机版】已自动打开浏览器：{}", url);
                } else {
                    log.info("【单机版】请手动在浏览器打开：{}", url);
                }
            } catch (Throwable e) {
                log.warn("【单机版】自动打开浏览器失败，请手动访问 {}（{}）", url, e.getMessage());
            }
        }, "open-browser").start();
    }
}
