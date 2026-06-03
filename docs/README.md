# 项目说明

## 1. 题目方向

后端方向

## 2. 功能清单

### 已完成功能

1. **工作项管理** - 完整的 CRUD 操作
2. **状态流转** - 基于状态机的状态流转引擎
3. **澄清问题管理** - 新增、查询、解决澄清问题
4. **AI 辅助分析** - Mock 实现，支持 5 种分析类型
5. **前端演示页面** - 原生 HTML/JS 实现

### 核心业务规则

- 高优先级未解决的澄清问题会阻断状态流转
- 状态流转历史完整记录
- 统一的错误处理和响应格式

## 3. 技术栈

- **后端框架**: Spring Boot 3.2.5
- **Java 版本**: Java 17+
- **数据库**: H2 (内存模式)
- **ORM**: Spring Data JPA
- **构建工具**: Maven
- **前端**: 原生 HTML/CSS/JavaScript

### 选择理由

1. **Spring Boot 3**: 最新稳定版本，性能优秀，生态完善
2. **H2**: 内嵌数据库，无需额外安装，便于快速启动和演示
3. **原生 HTML/JS**: 无需构建工具，直接在 Spring Boot 中提供静态页面

## 4. 如何运行

### 环境要求

- JDK 17+
- Maven 3.6+

### 启动步骤

```bash
# 进入项目目录
cd workitem-service

# 编译项目
mvn clean compile

# 启动应用
mvn spring-boot:run
```

### 访问应用

- **前端页面**: http://localhost:8080/
- **H2 控制台**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:workitemdb`
  - 用户名: `sa`
  - 密码: (空)

## 5. 如何测试

### 运行所有测试

```bash
mvn test
```

### 运行单个测试类

```bash
mvn test -Dtest=StatusTransitionServiceTest
```

### 运行单个测试方法

```bash
mvn test -Dtest=StatusTransitionServiceTest#transition_draftToAnalyzing_shouldSucceed
```

## 6. 核心设计说明

### 状态流转设计

采用枚举 + 规则映射的方式实现状态机：

```
DRAFT → ANALYZING → READY → IN_PROGRESS → TESTING → COMPLETED
  ↑         ↓         ↑          ↓
  +--------+---------+----------+
```

**合法流转规则**:
- DRAFT → ANALYZING
- ANALYZING → DRAFT, READY
- READY → ANALYZING, IN_PROGRESS
- IN_PROGRESS → READY, TESTING
- TESTING → IN_PROGRESS, COMPLETED
- COMPLETED → (终态，不可变更)

**阻断规则**:
- 存在未解决的高优先级澄清问题时，不允许进入 READY、IN_PROGRESS、TESTING 或 COMPLETED 状态

### API 设计

采用 RESTful 风格，资源划分清晰：

- `/api/work-items` - 工作项 CRUD
- `/api/work-items/{id}/transition` - 状态流转
- `/api/work-items/{id}/clarifications` - 澄清问题管理
- `/api/work-items/{id}/analyze` - AI 分析

### AI 服务设计

采用接口 + 实现的方式，便于替换：

```java
public interface AiAnalysisService {
    AiAnalysisResult analyze(WorkItem workItem, AnalysisType type);
}
```

当前使用 `MockAiAnalysisService` 实现，通过关键词匹配生成结构化结果。
替换为真实 LLM 只需实现该接口并注入即可。

## 7. 已完成内容

1. ✅ 工作项 CRUD
2. ✅ 状态流转引擎（含阻断规则）
3. ✅ 澄清问题管理
4. ✅ AI 分析服务（Mock 实现）
5. ✅ 前端演示页面
6. ✅ 单元测试
7. ✅ 统一异常处理
8. ✅ 种子数据

## 8. 未完成内容及原因

1. ❌ OpenAPI/Swagger 文档 - 时间有限，优先完成核心功能
2. ❌ 乐观锁/并发控制 - 当前为单用户演示场景
3. ❌ 认证/授权 - 题目不强制要求
4. ❌ Docker Compose - H2 内嵌数据库无需容器化

## 9. AI 使用说明

详见 [ai-usage.md](./ai-usage.md)

## 10. 后续优化方向

1. 接入真实 LLM 服务（如 OpenAI API）
2. 添加 OpenAPI/Swagger 文档
3. 实现乐观锁防止并发冲突
4. 添加用户认证和权限控制
5. 支持 PostgreSQL/MySQL 等生产级数据库
6. 完善前端页面（看板视图、拖拽流转等）
