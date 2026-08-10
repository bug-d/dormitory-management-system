# 高校宿舍管理系统

面向系统管理员、宿舍管理员和学生的前后端分离宿舍管理系统，包含用户、学生、宿舍、入住/调宿申请、审核和数据看板。

## 技术栈

- 后端：Java 17、Spring Boot 3.2、Spring Security、MyBatis-Plus、MySQL、JWT
- 前端：Vue 3、Vite、Element Plus、Pinia、Axios、ECharts

## 目录结构

```text
backend/    Spring Boot API
frontend/   Vue 3 管理界面
database/   建表与演示数据
docs/       需求、接口和部署说明
```

## 本地启动

复制 `.env.example` 为 `.env` 并替换占位值，或在当前终端设置同名环境变量。不要提交 `.env`。

数据库可使用 `docker compose up -d` 创建，也可以手动创建 MySQL 8 数据库后依次执行 `database/schema.sql` 和 `database/data.sql`。

### 后端

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$env:DB_URL = 'jdbc:mysql://localhost:3306/dormitory_management?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true'
$env:DB_USERNAME = 'root'
$env:DB_PASSWORD = '你的数据库密码'
$env:JWT_SECRET = '至少32个字符的随机密钥'

cd backend
mvn spring-boot:run
```

后端默认地址为 `http://localhost:8080/api`，接口文档为 `http://localhost:8080/api/doc.html`。

### 前端

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

前端默认地址为 `http://localhost:5173`。

## 构建验证

```powershell
cd backend
mvn test

cd ..\frontend
npm.cmd run build
```

## 安全说明

- 数据库凭据和 JWT 密钥只通过环境变量注入。
- `.env`、依赖目录和构建产物已被 `.gitignore` 排除。
- `database/data.sql` 仅用于本地演示，生产环境应删除或替换演示账号。

