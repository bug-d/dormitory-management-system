# 项目问题记录

## 2026-08-10 - Maven 默认绑定 Java 8

- 现象：编译时报 `无效的标记: --release`。
- 原因：系统默认 Maven 使用 Java 8，而 Spring Boot 3.2 要求 Java 17。
- 修复：构建前将 `JAVA_HOME` 和 `Path` 指向 `C:\Program Files\Java\jdk-17`。
- 预防：每次构建前用 `mvn -version` 确认 Java 版本。
- 相关文件：`backend/pom.xml`。

## 2026-08-10 - 迁移包包含生成目录和开发凭据

- 现象：项目包含 `node_modules`、`dist`、`target`，配置文件含开发环境凭据。
- 原因：迁移时复制了完整工作目录。
- 修复：完善 `.gitignore`，配置改用环境变量，首次提交前检查暂存清单。
- 预防：发布前运行 `git status --short` 和秘密扫描。
- 相关文件：`.gitignore`、`backend/src/main/resources/application.yml`。

## 2026-08-10 - ESLint Flat Config 不支持旧命令参数

- 现象：`npm run lint` 报 `Invalid option '--ext'`。
- 原因：项目使用 `eslint.config.js`，脚本仍传入旧配置模式的 `--ext` 和 `--ignore-path`。
- 修复：将脚本改为 `eslint . --fix`，由 Flat Config 决定文件范围和忽略项。
- 预防：升级 ESLint 配置模式时同步检查 package scripts。
- 相关文件：`frontend/package.json`、`frontend/eslint.config.js`。

## 2026-08-10 - 演示宿管未分配宿舍

- 现象：宿管登录和接口均正常，但宿舍列表为空。
- 原因：`database/data.sql` 创建了宿管用户，却没有写入 `manager_permissions`。
- 修复：为演示宿管分配 1 栋 4 间宿舍的 `full` 权限。
- 预防：演示数据初始化后对三个角色分别执行登录和核心列表冒烟检查。
- 相关文件：`database/data.sql`。

## 2026-08-10 - Docker 镜像下载可能中途断流

- 现象：首次拉取 `mysql:8.4` 出现 `short read` 和 `unexpected EOF`。
- 原因：大镜像下载期间网络连接中断。
- 修复：保留已缓存分层并重新执行 `docker pull mysql:8.4`，续传后成功。
- 预防：镜像拉取失败时先重试，不要删除已下载的 Docker 分层缓存。
