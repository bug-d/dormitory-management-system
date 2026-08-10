# 项目 SOP

## 开始检查

1. 阅读项目记忆文件。
2. 检查 Git 状态与当前分支。
3. 确认 Maven 使用 JDK 17。
4. 检查环境变量和数据库可用性。

## 开发规则

- 不在日志、文档、配置或提交中写入秘密。
- Controller 只做参数与权限边界，业务逻辑放在 Service。
- 前端页面通过 `src/api` 访问后端，不保留生产路径上的模拟数据。
- 新接口必须与前端 URL、HTTP 方法和返回结构一致。

## 验证清单

- `mvn test`
- `npm.cmd run build`
- 检查 TODO、空源码和前后端接口映射。
- 检查暂存文件中无 `node_modules`、`target`、`dist` 和秘密。

## 发布清单

- 更新 `DEV_LOG.md`。
- 检查 `git diff --cached`。
- 提交后推送到目标 GitHub 仓库。
- 首次发布确认 README 可独立指导本地启动。
