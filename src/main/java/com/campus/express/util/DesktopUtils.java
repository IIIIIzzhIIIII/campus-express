package com.campus.express.util;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.net.URI;

/**
 * 桌面相关工具：用系统默认浏览器打开地址。
 * 依次尝试 AWT Desktop 与系统命令，兼容 headless / 无桌面环境。
 */
public final class DesktopUtils {

    private DesktopUtils() {
    }

    public static boolean openUrl(String url) {
        if (openByDesktop(url)) {
            return true;
        }
        return openBySystemCommand(url);
    }

    private static boolean openByDesktop(String url) {
        try {
            if (!GraphicsEnvironment.isHeadless()
                    && Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                return true;
            }
        } catch (Throwable ignored) {
            // 继续尝试系统命令
        }
        return false;
    }

    private static boolean openBySystemCommand(String url) {
        try {
            String os = System.getProperty("os.name", "").toLowerCase();
            ProcessBuilder builder;
            if (os.contains("win")) {
                builder = new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url);
            } else if (os.contains("mac")) {
                builder = new ProcessBuilder("open", url);
            } else {
                builder = new ProcessBuilder("xdg-open", url);
            }
            builder.redirectErrorStream(true).start();
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
