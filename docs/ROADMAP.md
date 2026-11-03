# 学习路线（M0 → M5）

> 用法：每个里程碑先读「知识点」，再看代码，最后跑「验收」和「练习」。
> 卡住时先自己查 10 分钟，再来问"为什么"，而不是只要一份能跑的代码。

---

## M0 环境与工程骨架 ✅

**产出**

- JDK 21（Temurin）、Node 24、git、gh CLI
- `backend/`（Spring Initializr 生成，Maven Wrapper）与 `frontend/`（create-vite）同仓管理

**知识点**

| 主题 | 要弄懂什么 |
| --- | --- |
| JDK vs JRE | 为什么开发要装 JDK；`JAVA_HOME` 的作用 |
| Maven Wrapper | `mvnw` 为什么优于依赖本机 Maven：版本随仓库走，换机器不用重装 |
| Spring Boot starter | `spring-boot-starter-webmvc` 帮你自动装了什么 |
| Vite 与 webpack | 开发期 ESM 按需编译 vs 全量打包，为什么冷启动快 |
| 单仓（monorepo） | 前后端同仓的收益与代价 |

**验收**

```powershell
java -version          # 21.x
node -v                # >= 20
cd backend; .\mvnw.cmd -v
cd frontend; npm run build
```

---

## M1 前后端跑通 ✅

**产出**

- 后端 `/api/health`、统一响应 `ApiResponse<T>`、全局异常处理、Security 基线配置
- 前端路由、Pinia、axios 封装、`/api` 代理、首页健康检查卡片

**知识点（重点，后面全靠它）**

1. **同源策略与 CORS**：浏览器为什么拦截跨域？预检请求（OPTIONS）在什么时候发？
   - 本项目开发期用 Vite 代理：浏览器只看到 5173，同源，不触发 CORS。
   - 后端仍配置 CORS，是为了"万一前端直连 8080"和移动端调试。
   - `allowCredentials=true` 时 `allowedOrigins` 不能是 `*`。
2. **统一响应结构**：`code/message/data` 与 HTTP 状态码的分工；为什么不要所有业务错误都返回 200。
3. **REST 与资源命名**：`/api/health`、后续 `/api/todos/{id}` 的设计习惯。
4. **axios 拦截器**：请求拦截注入 Token、响应拦截解包 + 抛 `ApiError`，组件层不再写 try/catch 分支。
5. **Vue 3 组合式 API**：`<script setup>`、`ref` vs `reactive`、`onMounted`。
6. **Pinia setup store**：为什么用 `storeToRefs` 解构（保持响应性）。
7. **路由懒加载**：`() => import(...)` 与代码分割的关系。
8. **Spring Security 默认行为**：为什么加了 security 依赖后接口会 401；`permitAll` 与 `anyRequest().authenticated()` 的白名单思维。

**验收**

1. 打开 <http://localhost:5173>，卡片显示"服务正常"、Java 版本、时间。
2. 停掉后端再点「重新检测」，前端显示错误而不是白屏/控制台未捕获异常。
3. 访问 <http://localhost:8080/api/health> 正常；访问 `/api/nope` 返回 401（未登录）。
4. `frontend npm run build` 无类型错误。

**练习**

- 给 `/api/health` 加一个 `uptime` 字段（服务运行秒数），前端同步展示。
- 故意把后端 CORS 的 origin 改成 `http://localhost:9999`，用「前端直连 8080」的方式观察报错，理解 CORS 由谁执行。
- 阅读 `GlobalExceptionHandler`，说出为什么兜底异常不把 `ex.getMessage()` 返回给前端。

---

## M2 数据库与认证 ⬜

**目标**：能注册、登录、拿到 JWT，并用它访问受保护接口。

**知识点**

1. **Flyway 迁移**：`V1__init.sql` 的命名规则、`flyway_schema_history` 表、为什么不用 `ddl-auto=update`。
2. **JPA 映射**：`@Entity`、`@Id`、`@Column`、`Instant` 与 SQLite 的存储差异、`open-in-view=false` 的原因。
3. **Repository**：Spring Data 方法名派生查询、`Optional` 的用法。
4. **密码存储**：为什么必须 BCrypt/Argon2；加盐、成本因子；`PasswordEncoder` 的 `matches`。
5. **JWT 结构**：header.payload.signature；为什么 payload 不能放敏感信息；`exp`、签发者、密钥强度。
6. **Access Token vs Refresh Token**：有效期、撤销、存储位置（内存 vs httpOnly Cookie vs localStorage 的取舍）。
7. **Spring Security 过滤器链**：`OncePerRequestFilter` 放行逻辑、`SecurityContextHolder`、401 vs 403。
8. **DTO + Bean Validation**：`@NotBlank`、`@Email`、`@Size`，为什么不让实体直接进出接口。

**产出**

- `V2__auth.sql`（如需加索引/字段）
- `User` 实体、`UserRepository`、`AuthService`、`AuthController`
- `JwtService`、`JwtAuthFilter`
- 前端 `LoginView`、`RegisterView`、`user` store、路由守卫

**验收**

- 注册 → 登录返回 token → 携带 token 访问 `/api/auth/me` 成功
- 错误密码返回 401；重复用户名返回业务错误码
- 刷新页面后登录态保持（按选定的存储方案）

**练习**

- 限制密码长度 8–64，并给出中文错误提示。
- 把 token 有效期改成 2 分钟，观察过期后的 401 与前端跳转。
- 用 <https://jwt.io> 解码自己的 token，指出哪一段可被伪造、哪一段不能。

---

## M3 业务 CRUD ⬜

**目标**：一个完整的 Todo 模块（增删改查 + 分页 + 只操作自己的数据）。

**知识点**

1. REST 语义：`POST /todos`、`PATCH /todos/{id}`、`DELETE` 的幂等性讨论。
2. 分页：`Pageable`、`Page<T>`、`PageResponse` 与前端分页器。
3. 越权防护：为什么查询条件必须带 `userId`，而不是"查到再判断"。
4. 乐观锁：`@Version` 解决并发覆盖。
5. 前端表单与校验：受控输入、提交态、错误提示。
6. 列表渲染与性能：`v-for :key`、`computed` 缓存、`watch` vs `watchEffect`。

---

## M4 安全与质量 ⬜

**知识点**

1. 输入校验与输出编码（XSS 的真正防线在前端渲染，不在"过滤关键字"）。
2. CSRF 什么时候才有意义（Cookie 会话）vs JWT 放在 Header。
3. 限流（Bucket4j / 网关）、登录失败锁定、时序攻击。
4. 安全响应头：CSP、HSTS、X-Content-Type-Options、Referrer-Policy。
5. SQL 注入与参数化查询；JPA 原生 SQL 的正确写法。
6. 日志脱敏：密码、token、手机号不能进日志。
7. 测试金字塔：单元测试（Service）、切片测试（`@WebMvcTest`）、集成测试（`@SpringBootTest`）。
8. GitHub Actions：缓存依赖、并行任务、构建产物。

---

## M5 部署与迁移 ⬜

**知识点**

1. 多阶段构建 Dockerfile；`dist` 交给 Nginx，Spring Boot 打成可执行 jar。
2. Nginx：静态资源、`/api` 反代、`try_files` 支持 history 路由、gzip/brotli。
3. HTTPS：Let's Encrypt、证书续期、HSTS。
4. 配置外置：环境变量覆盖 `application.yml`，密钥不进仓库。
5. PostgreSQL 迁移：Flyway 多数据库脚本策略、连接池、索引与执行计划（`EXPLAIN`）。
6. 可观测性：Actuator 健康检查、结构化日志、慢查询。
7. 备份与回滚：数据库备份、镜像回滚。

---

## 复习清单（随时自测）

- [ ] 说出一次浏览器输入 URL 到页面渲染，前后端各发生了什么
- [ ] 为什么开发期用代理而生产期用 Nginx，两者解决的是同一个问题吗
- [ ] 401 与 403 的区别，什么场景下前端该跳登录、什么场景只提示无权限
- [ ] JWT 无法"主动失效"，怎么办
- [ ] 为什么表结构变更要写迁移脚本，而不是让 ORM 自动改表
- [ ] `open-in-view=false` 之后懒加载为什么会报错，正确做法是什么
