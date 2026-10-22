# 单机版与 exe 运行说明

> 目标：在**没有安装 MySQL、没有安装 JDK** 的电脑上，也能直接演示整套系统。
> 实现方式：Spring Boot `standalone` 配置 + H2 文件数据库 + jpackage 内置 JRE。

---

## 1. 与标准版（MySQL）的区别

| 项目 | 标准版 | 单机版 |
| --- | --- | --- |
| 数据库 | MySQL 8.0 | H2 2.x（文件模式，`MODE=MySQL`） |
| JDK | 需安装 JDK 17+ | exe 内置 JRE，无需安装 |
| 配置文件 | `application.yml` | `application-standalone.yml` |
| 启动参数 | 无 | `--spring.profiles.active=standalone` |
| 数据位置 | MySQL 数据目录 | `%USERPROFILE%\.campus-express\data` |
| 首次启动 | 手动执行 `sql/schema.sql`、`sql/data.sql` | 自动建表并导入演示数据 |

两种模式**共用同一套 Java 代码与页面**。`PackageMapper` 中的统计 SQL 已改写为
`CAST(... AS DATE)` / `COALESCE(...)` 等 MySQL、H2 都支持的标准写法；
分页插件不再写死 `DbType.MYSQL`，改为按 JDBC URL 自动识别。

---

## 2. 源码方式运行单机版

```bat
:: 1. 打包（需 JDK 17+）
mvnw -DskipTests package

:: 2. 启动单机版
java -jar target\campus-express.jar --spring.profiles.active=standalone
```

启动日志出现下面两行即表示数据库就绪：

```
【单机版】首次启动，正在初始化 H2 数据库 ...
【单机版】数据库初始化完成，演示账号：admin/123456、stu001/123456
```

浏览器会自动打开 <http://localhost:8081/login>；若未自动打开，手动访问即可。

---

## 3. 打包 exe（内置 JRE）

前置条件：安装 **JDK 17 及以上完整版**（需带 `jpackage`、`jlink`），并确保 `JAVA_HOME` 指向它。

```powershell
# 默认使用 %JAVA_HOME%，版本号 1.0.0
powershell -ExecutionPolicy Bypass -File scripts\build-exe.ps1

# 指定 JDK 与版本号
powershell -ExecutionPolicy Bypass -File scripts\build-exe.ps1 -JdkHome "C:\Program Files\Java\jdk-17" -Version 1.0.1
```

产物：

```
dist\
├── CampusExpress\
│   ├── CampusExpress.exe      ← 双击运行
│   ├── app\                   ← 程序 jar 与启动配置
│   ├── runtime\               ← 内置 JRE
│   └── 使用说明.md
└── CampusExpress-1.0.0-win-x64.zip
```

把 `CampusExpress` 整个文件夹（或 zip 解压后的内容）复制到任意 Windows 电脑，
**双击 `CampusExpress.exe` 即可运行**，不需要安装 JDK、MySQL，也不需要联网。

程序启动后：

- 自动打开浏览器进入登录页；
- **任务栏右下角出现托盘图标**：双击图标可重新打开系统，**右键 → 退出** 可正常关闭程序（推荐用这种方式退出）。

> 首次启动约 3~8 秒（解压 / 初始化数据库），之后会快很多。
> 若 Windows 防火墙 / 杀毒软件提示，请选择「允许访问」。
> 如果托盘没有出现，可在任务管理器中结束 `CampusExpress.exe`。

---

## 4. 数据文件、备份与重置

| 内容 | 路径 |
| --- | --- |
| 数据库文件 | `%USERPROFILE%\.campus-express\data\campus_express.mv.db` |
| 运行日志 | `%USERPROFILE%\.campus-express\logs\campus-express.log` |

- **备份**：关闭程序后，复制整个 `%USERPROFILE%\.campus-express` 目录即可。
- **恢复**：关闭程序，把备份目录放回原位置。
- **重置演示数据**：关闭程序，删除 `%USERPROFILE%\.campus-express` 目录，再次启动会自动重新建库并导入演示数据。
- 程序**只会在数据库为空时初始化**，不会覆盖已经录入的数据。

---

## 5. 修改端口 / 启动参数

- 源码运行：修改 `src/main/resources/application-standalone.yml` 中的 `server.port`，
  或启动时追加 `--server.port=9090`。
- exe 运行：编辑 `CampusExpress\app\CampusExpress.cfg`，在 `java-options` 中追加：

  ```
  java-options=--server.port=9090
  ```

  同时记得修改 `java-options=-Dcampus.open-browser=true`（默认已开启自动打开浏览器）。

---

## 6. 默认账号

| 端 | 账号 | 密码 |
| --- | --- | --- |
| 后台管理 | admin | 123456 |
| 后台管理（员工） | staff / lisi | 123456 |
| 用户端（学生） | stu001 / stu002 | 123456 |

---

## 7. 常见问题

**Q1：双击 exe 没反应？**
查看 `%USERPROFILE%\.campus-express\logs\campus-express.log`；
最常见原因是 8081 端口被占用，可换端口或关闭占用进程（`netstat -ano | findstr 8081`）。

**Q2：提示「数据库已被锁定」/ `Database may be already in use`？**
说明已经有一个实例在运行（H2 文件库同一时间只允许一个进程访问）。关闭已有窗口后重试。

**Q3：换电脑后数据不见了？**
数据保存在用户目录，不会随 exe 一起复制。按第 4 节复制 `.campus-express` 目录即可迁移。

**Q4：想改回 MySQL 运行？**
不带 `--spring.profiles.active=standalone` 启动，并按 README 的「快速开始（MySQL 标准版）」初始化数据库即可。

**Q5：H2 脚本在哪里？如何重新生成？**
`src/main/resources/db/h2/schema-h2.sql`、`data-h2.sql` 由
`scripts/gen-h2-sql.ps1` 从 `sql/*.sql` 自动转换生成。修改 MySQL 脚本后可执行：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\gen-h2-sql.ps1
```
