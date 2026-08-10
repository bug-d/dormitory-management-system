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

`database/data.sql` 是完整演示种子，会清空现有业务数据后写入 29 个用户、24 名学生、16 间宿舍以及各种状态的入住/调宿记录，只能用于本地开发和测试。

### 演示账号

所有演示账号密码均为 `123456`：

| 场景 | 用户名 | 说明 |
| --- | --- | --- |
| 系统管理员 | `admin` | 用户、学生、宿舍和审核管理 |
| 审核管理员 | `auditor` | 包含历史审核操作 |
| 男生宿管 | `manager` | 完全管理 1栋、2栋 |
| 女生宿管 | `manager_female` | 完全管理 3栋、4栋 |
| 只读宿管 | `manager_readonly` | 可查看全部宿舍，不可修改 |
| 已入住学生 | `student` | 当前入住 1栋101-A |
| 调宿审核中 | `student05` | 保留原床位并有一条待审核调宿申请 |
| 入住审核中 | `student08` | 没有当前宿舍，存在待审核入住申请 |
| 已停用学生 | `student23` | 用于验证禁用账号无法登录 |

从旧版本升级且曾使用过入住/调宿申请时，请备份数据库后执行：

```powershell
mysql -u root -p < database/migrations/2026-08-10-fix-assignment-occupancy.sql
```

该迁移会按有效入住记录重新校准宿舍人数，并补充床位审核索引。

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
