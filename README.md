#  AIGC 服务

基于 Java 17、Spring Boot 3.4.5、Spring AI 和 DeepSeek 构建的独立 AI 微服务。服务提供
RAG、会话记忆、多 Agent 路由、流式对话和语音交互能力，并通过网关调用天机学堂的课程、
交易等业务接口。

## 运行前准备

1. 使用 Java 17。
2. 创建 MySQL 数据库 `tj_aigc`，依次执行：
   - `src/main/resources/sql/chat_record.sql`
   - `src/main/resources/sql/chat_session.sql`
3. 准备支持 RediSearch 的 Redis Stack。当前默认会话记忆和向量库都使用 Redis。
4. 根据实际使用能力设置环境变量：

| 环境变量 | 用途 |
| --- | --- |
| `TJ_MYSQL_USERNAME`、`TJ_MYSQL_PASSWORD` | AIGC 数据库 |
| `TJ_MYSQL_HOST`、`TJ_MYSQL_PORT` | MySQL 地址，默认 `127.0.0.1:3306` |
| `TJ_REDIS_HOST`、`TJ_REDIS_PORT` | Redis Stack 地址，默认 `127.0.0.1:6379` |
| `TJ_REDIS_PASSWORD` | Redis 密码 |
| `DEEPSEEK_API_KEY` | DeepSeek 聊天、路由和文本处理 |
| `DEEPSEEK_MODEL` | 可选，默认 `deepseek-v4-flash` |
| `DEEPSEEK_BASE_URL` | 可选，默认 DeepSeek 官方 API |
| `DASHSCOPE_API_KEY` | 通义向量嵌入和语音能力 |
| `OSS_ACCESSKEY_ID`、`OSS_ACCESSKEY_SECRET` | 音频文件上传 |
| `NACOS_SERVER_ADDR`、`NACOS_NAMESPACE` | Nacos 地址与命名空间 |
| `NACOS_USERNAME`、`NACOS_PASSWORD` | Nacos 鉴权（启用时） |
| `TJ_GATEWAY_URL` | 天机学堂网关地址，默认 `http://127.0.0.1:10010` |

可以复制 `.env.example` 后按本地环境填写变量。不要把 `.env` 或任何真实密钥提交到仓库。
生产环境应至少显式设置 `SPRING_PROFILES_ACTIVE=dev` 和 `AIGC_DISCOVERY_IP`。

## 构建和启动

```bash
sdk env
mvn clean test
mvn -DskipTests package
java -jar target/tj-aigc.jar
```

也可以使用 Docker 构建：

```bash
docker build -t tj-aigc .
docker run --env-file .env -p 8094:8094 tj-aigc
```

服务默认监听 `8094`，Nacos 服务名为 `aigc-service`。外部请求统一经过网关：

```text
/ais/** -> aigc-service/**
```

例如 AI 会话地址为 `/ais/chat`，会话管理地址为 `/ais/session`。

## 配置说明

- 系统提示词优先从 Nacos 读取；未配置对应 dataId 时使用 `application.yml` 中的本地兜底文本。
- `ENHANCE`、`ROUTE` 和 `/chat/text` 均使用 DeepSeek；默认模型为 `deepseek-v4-flash`。
- 现有智能体依赖连续工具调用，DeepSeek V4 请求默认使用非思考模式。
- DeepSeek 不提供当前项目需要的向量嵌入和语音接口，因此这两项能力继续使用通义。
- 登录请求由网关写入 `user-info`，AIGC 服务会把用户身份和原始令牌继续传给课程、交易接口。
- Nacos namespace、注册地址及业务网关地址均通过环境变量注入，仓库不保存具体环境配置。
- 外部模型、MySQL、Redis Stack 和 OSS 均属于运行依赖，缺少相应配置时只能验证基础启动，不能验证完整聊天链路。
