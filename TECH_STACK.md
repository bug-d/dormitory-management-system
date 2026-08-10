# 技术栈

## 运行时

- JDK 17
- Maven 3.9+
- Node.js + npm
- MySQL 8

## 框架与库

- Spring Boot 3.2、Spring Security、MyBatis-Plus、JJWT、Knife4j
- Vue 3、Vite 5、Element Plus、Pinia、Axios、ECharts

## 命令

```powershell
# 后端构建
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
cd backend
mvn test

# 前端安装与构建
cd frontend
npm.cmd install
npm.cmd run build

# 开发运行
npm.cmd run dev
mvn spring-boot:run
```

## 环境变量

- `DB_URL`：MySQL JDBC 地址。
- `DB_USERNAME`：数据库用户名。
- `DB_PASSWORD`：数据库密码。
- `JWT_SECRET`：至少 32 字节的 JWT HMAC 密钥。
- `JWT_EXPIRATION`：Token 有效期（毫秒）。
- `VITE_API_BASE_URL`：前端 API 基础路径。

## 外部服务

- MySQL 8
- GitHub：`bug-d/dormitory-management-system`
