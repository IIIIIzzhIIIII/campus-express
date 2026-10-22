package com.campus.express.config;

import com.campus.express.util.DesktopUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;

/**
 * 单机版系统托盘：双击托盘图标打开系统，右键菜单可退出程序。
 * 双击 exe 运行没有控制台窗口，通过托盘提供正常的关闭入口。
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "campus.tray-icon", havingValue = "true")
public class TrayIconService {

    @Value("${server.port:8081}")
    private int port;

    private TrayIcon trayIcon;

    @EventListener(ApplicationReadyEvent.class)
    public void install() {
        if (GraphicsEnvironment.isHeadless() || !SystemTray.isSupported()) {
            log.info("【单机版】当前环境不支持系统托盘，退出程序请在任务管理器中结束 CampusExpress");
            return;
        }
        try {
            String url = "http://localhost:" + port + "/login";

            PopupMenu menu = new PopupMenu();
            MenuItem open = new MenuItem("打开系统");
            open.addActionListener(e -> DesktopUtils.openUrl(url));
            MenuItem exit = new MenuItem("退出");
            exit.addActionListener(e -> {
                if (trayIcon != null) {
                    SystemTray.getSystemTray().remove(trayIcon);
                }
                log.info("【单机版】用户从托盘退出程序");
                System.exit(0);
            });
            menu.add(open);
            menu.addSeparator();
            menu.add(exit);

            trayIcon = new TrayIcon(createIcon(), "校园快递驿站（双击打开，右键退出）", menu);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(e -> DesktopUtils.openUrl(url));
            SystemTray.getSystemTray().add(trayIcon);
            log.info("【单机版】已添加系统托盘图标，右键可退出程序");
        } catch (Throwable e) {
            log.warn("【单机版】创建系统托盘失败：{}", e.getMessage());
        }
    }

    private Image createIcon() {
        int size = 32;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(0x2F6FED));
            g.fillRoundRect(0, 0, size, size, 8, 8);
            g.setColor(Color.WHITE);
            g.fillRoundRect(7, 10, 18, 14, 3, 3);
            g.setColor(new Color(0x2F6FED));
            g.fillRect(15, 10, 2, 14);
        } finally {
            g.dispose();
        }
        return image;
    }
}
