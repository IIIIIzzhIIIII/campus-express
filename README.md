# 校园快递驿站管理系统

一个基于 Spring Boot 3 + Thymeleaf + MySQL 的校园快递驿站管理系统，解决驿站快件登记混乱、取件通知不规范、问题件难追溯等问题。

> **两种运行方式**
> - **标准版**：MySQL 8.0 + JDK 17（适合开发、答辩演示环境已装数据库的情况）
> - **单机版 / exe**：内置 H2 文件数据库 + 内置 JRE，**双击即用，无需安装 MySQL / JDK**（见 [单机版与 exe 运行说明](docs/05-单机版与exe运行说明.md)）

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 后端 | Spring Boot 3.2.5、Spring MVC、MyBatis-Plus 3.5.7、MySQL 8.0 / H2 2.x |
| 前端 | Thymeleaf、Bootstrap 5、ECharts 5、jQuery |
| 安全 | Session + 拦截器 + BCrypt 密码加密 + 自定义 `@RequiresRole` 角色权限 |
| 文档 | Knife4j（OpenAPI3），在线地址 `/doc.html` |
| 其他 | Lombok、Hutool、EasyExcel（导出）、AOP 操作日志 |

## 功能清单

1. **快件入库登记**：录入快递单号、快递公司、收件人信息，自动生成 6 位取件码、自动分配货架，支持扫码枪连续录入。
2. **取件通知 / 取件确认**：一键发送通知（模拟短信写日志），学生凭取件码 / 手机号后四位+单号 / 扫码枪三种方式取件，系统标记已取件。
3. **快件查询**：后台多条件查询 + 学生自助查询页（手机号 / 快递单号）。
4. **问题件管理**：滞留、错件、破损、丢件、拒收、退回、其他 7 类问题件的登记与处理。
5. **用户管理**：驿站管理员账号，区分超级管理员 / 驿站员工，细粒度权限控制。
6. **数据统计**：每日入库 / 取件数量、月度趋势、快递公司占比、操作员业绩，支持 Excel 导出。
7. **日志记录**：登录日志 + 操作日志（AOP 自动记录），快件状态流转全程留痕。
8. **扩展功能**：大屏看板、滞留件定时任务自动标记、手机端自适应。
9. **用户端（学生）注册登录**：用户端账号注册、账号密码登录、资料与密码修改，登录后可查看「我的快递」及取件码。
10. **在线寄件与物流跟踪**：学生在线下单寄件 → 驿站揽收（回填运单号）→ 运输中 / 派送中 / 已签收，寄件人可在「我的寄件」、收件人可在「我的收件」查看物流轨迹时间轴（按收件手机号匹配）。
11. **存件（寄存）服务**：学生提交寄存申请获得 6 位存件码 → 驿站接收上架（在库）→ 凭存件码取出。

## 环境要求

- JDK 17 及以上（**JDK 17 / 21 / 25 / 26 均已实测可编译运行**）
- Maven 3.6+（或使用自带的 `mvnw` Wrapper，无需安装 Maven）
- MySQL 8.0（**仅标准版需要**；单机版使用内置 H2，无需安装数据库）

## 零依赖单机版（H2，推荐演示使用）

不安装 MySQL 也能直接运行，数据保存在 `%USERPROFILE%\.campus-express\data`：

```bash
# 1. 打包
mvnw -DskipTests package

# 2. 以 standalone 配置启动（首次启动自动建表 + 导入演示数据）
java -jar target/campus-express.jar --spring.profiles.active=standalone
```

启动后自动打开 http://localhost:8081/login （账号 `admin / 123456`）。

打包成 **内置 JRE 的 exe**（目标机器无需安装 JDK）：

```powershell
powershell -ExecutionPolicy Bypass -File scripts\build-exe.ps1
# 产物：dist\CampusExpress\CampusExpress.exe
#       dist\CampusExpress-1.0.0-win-x64.zip
```

详细说明（数据备份、重置、改端口、常见问题）见 [docs/05-单机版与exe运行说明.md](docs/05-单机版与exe运行说明.md)。

## 快速开始（MySQL 标准版）

### 1. 初始化数据库

数据库账号默认 `root / 123456`，执行以下两个脚本（已内置 38 条快件等测试数据）：

```sql
-- 方式一：命令行
mysql -uroot -p123456 --default-character-set=utf8mb4 < sql/schema.sql
mysql -uroot -p123456 --default-character-set=utf8mb4 < sql/data.sql

-- 方式二：用 Navicat 依次执行 sql/schema.sql、sql/data.sql
```

> **已经初始化过数据库的项目**（只想补上用户端模块）执行一次即可，脚本可重复执行：
> ```bash
> mysql -uroot -p123456 --default-character-set=utf8mb4 < sql/upgrade-student-module.sql
> ```

### 2. 修改数据库连接（如需）

编辑 `src/main/resources/application.yml` 中的 `spring.datasource.username/password`。

### 3. 启动

```bash
# 方式一：Maven Wrapper（无需安装 Maven）
mvnw spring-boot:run

# 方式二：本地已安装 Maven
mvn spring-boot:run
```

> **注意 JDK 版本**：Spring Boot 3 必须使用 **JDK 17 及以上**编译运行。若命令行 `java -version` 显示 1.8，请先设置：
> ```bat
> set JAVA_HOME=C:\Program Files\Java\jdk-17
> set PATH=%JAVA_HOME%\bin;%PATH%
> ```
> 或在 IDEA 的 `File → Project Structure → Project SDK` 中选择 JDK 17+。

### 4. 访问

> **端口以 `src/main/resources/application.yml` 里的 `server.port` 为准，当前配置为 `8081`。**

- 后台首页：http://localhost:8081/index
- 接口文档：http://localhost:8081/doc.html
- 大屏看板：http://localhost:8081/screen
- 后台寄件订单：http://localhost:8081/order/send
- 后台存件管理：http://localhost:8081/order/deposit
- 用户端（学生）首页：http://localhost:8081/student（快件查询，免登录；右上角提供登录 / 注册入口）
- 用户端个人中心：http://localhost:8081/student/center（登录后：我的快递 / 我的收件 / 我的寄件 / 我要寄件 / 我的存件 / 我要存件）
- 学生自助查询（免登录，独立页面）：http://localhost:8081/student/query

### 5. 默认账号

| 账号 | 密码 | 角色 |
| --- | --- | --- |
| admin | 123456 | 超级管理员 |
| staff | 123456 | 驿站员工 |
| lisi | 123456 | 驿站员工 |

用户端（学生端 http://localhost:8081/student ）账号：

| 账号 | 密码 | 说明 |
| --- | --- | --- |
| stu001 | 123456 | 演示学生（手机号 13812340001，有收件记录） |
| stu002 | 123456 | 演示学生 |
| 自行注册 | - | 用户端首页右上角「注册」按钮进入注册页，注册后可登录 |

## 目录结构

```
campus-express
├── sql/                  # MySQL 建库建表 + 测试数据
├── scripts/              # 构建脚本（生成 H2 脚本、打包 exe）
├── docs/                 # 需求说明、数据库设计、接口文档、部署说明
├── src/main/java/com/campus/express/
│   ├── common/           # 统一返回、异常、枚举、注解
│   ├── config/           # MyBatis-Plus、WebMvc、Knife4j、Jackson
│   ├── interceptor/      # 登录 + 权限拦截器
│   ├── aspect/           # 操作日志 AOP
│   ├── controller/       # 控制层（页面 + REST API）
│   ├── service/          # 业务层
│   ├── mapper/           # 持久层
│   ├── entity/ dto/ vo/  # 实体 / 请求 / 返回对象
│   ├── task/             # 定时任务（滞留件标记）
│   └── util/             # 工具类
└── src/main/resources/
    ├── application.yml            # 标准版（MySQL）配置
    ├── application-standalone.yml # 单机版（H2）配置
    ├── db/h2/                     # H2 建表 / 初始化数据脚本
    ├── static/           # 前端静态资源（Bootstrap/ECharts/jQuery 本地化）
    └── templates/        # Thymeleaf 页面
```

## 常见问题

- **访问 `http://localhost:8080/student` 出现 Whitelabel Error Page（`status=404`）**：
  访问的端口不对。本项目 `application.yml` 里配置的端口是 **8081**，而本机 8080 已被其它程序占用，
  8080 上返回的 404 与该项目无关。请改用 `http://localhost:8081/student`，或把 `server.port` 改回 8080（需先关掉占用 8080 的进程）。
- **端口被占用**：改 `application.yml` 的 `server.port`，或用 `--server.port=8081` 启动参数。
- **页面（/login、/student）返回 500，但 /api 接口正常**：说明程序启动后 `target/classes` 被清理过（执行了 `mvn clean` / IDEA Rebuild），
  运行中的程序读不到 `templates` 模板。在 IDEA 里 **停止后重新运行** 即可，不要一边运行一边 clean。
- **浏览器报 `xxx is not defined`（例如 `toTimeStr is not defined`）或页面样式/脚本没更新**：
  这是浏览器缓存了旧的 `common.js` / `common.css`。项目已做两层防护：
  ① `application.yml` 里设置了静态资源协商缓存（`Cache-Control: no-cache`，改完刷新即生效）；
  ② 页面引用静态资源时带了版本号（`/js/common.js?v=20260915`）。
  若仍出现，按 **`Ctrl + Shift + R`** 强制刷新一次即可。
  **以后修改 `common.js` / `common.css` 后**，把 `fragments/layout.html`、`fragments/student.html` 中的
  `v='20260915'` 换成新的版本号（如日期 `v='20260916'`），可确保所有浏览器立刻拿到新文件。
- **数据库连不上**：确认 MySQL 服务 `MySQL80` 已启动，账号密码正确；也可以直接用单机版：
  `java -jar target/campus-express.jar --spring.profiles.active=standalone`。
- **想清空单机版数据重新演示**：关闭程序后删除 `%USERPROFILE%\.campus-express` 目录，下次启动会自动重新建库并导入演示数据。
- **单机版端口被占用**：默认 8081，修改 `src/main/resources/application-standalone.yml` 的 `server.port`
  （exe 版则修改 `CampusExpress\app\CampusExpress.cfg` 中的 `--server.port=...`）。
- **IDEA 编译报 `java.lang.ExceptionInInitializerError` / `com.sun.tools.javac.code.TypeTag :: UNKNOWN` / 大量「找不到符号」**：
  这是 **JDK 23+ 默认不再从 classpath 自动发现注解处理器**，导致 Lombok 未生效。
  本项目已在 `pom.xml` 中通过 `maven-compiler-plugin` 的 `annotationProcessorPaths` 显式声明 Lombok 修复，
  并把 Lombok 固定为 `1.18.48`（支持 JDK 25/26/27）。若仍报错，请执行 **IDEA → File → Invalidate Caches / Restart**，
  或 `mvn clean compile` 重新编译。
