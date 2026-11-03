# 清河 · 全栈学习项目

一个从零搭建、边写边学的**前后端分离**网站系统。前端 Vue 3 + Vite + TypeScript，后端 Spring Boot 4 + Java 21，数据库从 SQLite 起步，后期迁移到 PostgreSQL。

> 这个仓库既是可运行的代码，也是学习笔记：每个里程碑（M0–M5）都能独立验收，代码里有关键设计的原因注释。

## 技术栈

| 层 | 选型 | 说明 |
| --- | --- | --- |
| 前端框架 | Vue 3.5（`<script setup>`） | 组合式 API，类型推导好 |
| 构建工具 | Vite 8 | 开发期秒级热更新，生产期 Rollup 打包 |
| 语言 | TypeScript 6（strict） | 前后端共享"类型即契约"的思路 |
| 状态管理 | Pinia | 组合式 store，替代 Vuex |
| 路由 | Vue Router 4 | 懒加载 + 路由守卫 |
| HTTP | axios（封装） | 统一响应解包、错误归一、Token 注入 |
| 后端框架 | Spring Boot 4.1 / Spring Framework 7 | 模块化 starter、虚拟线程可用 |
| 语言 | Java 21（LTS） | record、模式匹配、虚拟线程 |
| 安全 | Spring Security + JWT | 无状态鉴权、BCrypt 密码哈希 |
| 持久层 | Spring Data JPA / Hibernate 7 | 后续可平滑切换数据库 |
| 迁移 | Flyway | 表结构变更全部版本化 |
| 数据库 | SQLite → PostgreSQL | 开发零安装，上线可扩展 |

## 目录结构

```
qh/
├─ backend/                 Spring Boot 服务
│  ├─ src/main/java/com/sixlv6/backend/
│  │  ├─ api/               控制器（按资源划分）
│  │  ├─ common/            统一响应、异常、通用工具
│  │  ├─ config/            安全、CORS、序列化等配置
│  │  └─ ...
│  ├─ src/main/resources/
│  │  ├─ application.yml    默认配置（dev 生效）
│  │  └─ db/migration/      Flyway 迁移脚本 V1__xxx.sql
│  └─ data/                 SQLite 数据文件（git 忽略）
├─ frontend/                Vue 单页应用
│  ├─ src/api/              axios 实例与接口函数
│  ├─ src/router/           路由表
│  ├─ src/stores/           Pinia stores
│  ├─ src/views/            页面级组件
│  └─ vite.config.ts        别名 + /api 代理
├─ docs/ROADMAP.md          学习路线、知识点与验收清单
└─ README.md
```

## 环境要求

- JDK 21（本项目使用 `C:\tools\jdk21`，`JAVA_HOME` 已配置）
- Node.js ≥ 20（当前 v24）+ npm
- 无需单独安装 Maven：用仓库里的 `mvnw`

## 快速开始

**1. 启动后端（端口 8080）**

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

首次启动会自动创建 `backend/data/app.db` 并执行 Flyway 迁移。

**2. 启动前端（端口 5173）**

```powershell
cd frontend
npm install
npm run dev
```

浏览器打开 <http://localhost:5173>，首页的「后端连通性」显示**服务正常**即代表前后端已打通。

**3. 验证接口**

```powershell
curl http://localhost:8080/api/health
```

```json
{ "code": 0, "message": "ok", "data": { "status": "UP", "service": "qinghe-backend", "java": "21.0.13", "time": "..." } }
```

## 关键约定

1. **统一响应体**：`{ code, message, data }`，`code = 0` 表示业务成功；HTTP 状态码负责表达协议层结果。
2. **错误归一**：后端异常统一由 `GlobalExceptionHandler` 转 JSON；前端把网络/业务错误统一成 `ApiError`，组件只处理一种错误类型。
3. **开发期跨域**：浏览器只访问 5173，`/api` 由 Vite 代理转发到 8080，因此本地不需要放宽 CORS；生产环境由 Nginx 同源转发。
4. **表结构变更**：只改 `db/migration/V*.sql`，不用 `ddl-auto=update`，保证任何环境都能重建出一致的库。

## 学习路线

详见 [docs/ROADMAP.md](docs/ROADMAP.md)。

- [x] M0 环境与工程骨架
- [x] M1 前后端跑通（代理、统一响应、全局异常、健康检查）
- [ ] M2 数据库与认证（Flyway、JPA、BCrypt、JWT）
- [ ] M3 业务 CRUD（Todo、分页、前端页面）
- [ ] M4 安全与质量（限流、校验、测试、CI）
- [ ] M5 部署与迁移（Docker、Nginx、HTTPS、PostgreSQL）
