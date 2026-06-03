# API 设计说明

## 1. API 设计目标

支持前端页面的以下能力：
- 工作项的增删改查
- 工作项状态流转
- 澄清问题的管理
- AI 分析的触发和结果查询

## 2. 资源或模块划分

| 模块 | 路径前缀 | 说明 |
|------|----------|------|
| 工作项 | `/api/work-items` | 工作项的核心 CRUD 和状态管理 |
| 澄清问题 | `/api/work-items/{id}/clarifications` | 澄清问题的增删改查 |
| AI 分析 | `/api/work-items/{id}/analyze` | AI 分析触发和历史查询 |

## 3. API 列表

### 工作项 API

| 能力 | 方法/路径 | 输入摘要 | 输出摘要 | 说明 |
|------|-----------|----------|----------|------|
| 创建工作项 | `POST /api/work-items` | title, type, priority 等 | 工作项对象 | 状态默认为 DRAFT |
| 获取列表 | `GET /api/work-items` | status, type, priority 筛选 | 工作项数组 | 支持可选筛选 |
| 获取详情 | `GET /api/work-items/{id}` | 路径参数 id | 工作项对象 | 包含关联数据 |
| 更新工作项 | `PUT /api/work-items/{id}` | 需要更新的字段 | 工作项对象 | 部分更新 |
| 删除工作项 | `DELETE /api/work-items/{id}` | 路径参数 id | 无 | 物理删除 |

### 状态流转 API

| 能力 | 方法/路径 | 输入摘要 | 输出摘要 | 说明 |
|------|-----------|----------|----------|------|
| 执行流转 | `POST /api/work-items/{id}/transition` | targetStatus, reason | 状态历史记录 | 验证流转合法性 |
| 查询历史 | `GET /api/work-items/{id}/history` | 无 | 状态历史数组 | 按时间倒序 |
| 查询允许流转 | `GET /api/work-items/{id}/allowed-transitions` | 无 | 状态数组 | 当前状态可流转的目标 |

### 澄清问题 API

| 能力 | 方法/路径 | 输入摘要 | 输出摘要 | 说明 |
|------|-----------|----------|----------|------|
| 新增问题 | `POST /api/work-items/{id}/clarifications` | content, severity | 澄清问题对象 | 默认未解决 |
| 查询列表 | `GET /api/work-items/{id}/clarifications` | unresolved 筛选 | 澄清问题数组 | 支持筛选 |
| 更新问题 | `PUT /api/work-items/{id}/clarifications/{qid}` | status, answer | 澄清问题对象 | 用于解决问题 |

### AI 分析 API

| 能力 | 方法/路径 | 输入摘要 | 输出摘要 | 说明 |
|------|-----------|----------|----------|------|
| 触发分析 | `POST /api/work-items/{id}/analyze` | type | 分析结果对象 | 5 种分析类型 |
| 查询历史 | `GET /api/work-items/{id}/analyze` | 无 | 分析结果数组 | 按时间倒序 |

## 4. 状态流转错误设计

### 非法状态流转

```json
{
  "code": 400,
  "message": "非法的状态流转: DRAFT → IN_PROGRESS。允许的流转: [ANALYZING]"
}
```

### 业务规则阻断

```json
{
  "code": 400,
  "message": "存在 1 个未解决的高优先级澄清问题，无法继续流转。问题: 需求不明确"
}
```

### 资源不存在

```json
{
  "code": 404,
  "message": "工作项 不存在，ID: 999"
}
```

### 参数校验失败

```json
{
  "code": 400,
  "message": "参数校验失败",
  "data": {
    "title": "标题不能为空"
  }
}
```

## 5. AI 分析结果设计

分析结果以 JSON 格式存储，不同分析类型有不同的结构：

### 需求摘要 (SUMMARY)

```json
{
  "type": "SUMMARY",
  "title": "工作项标题",
  "summary": "提取的关键信息摘要",
  "workItemType": "STORY",
  "priority": "P1"
}
```

### 验收标准 (ACCEPTANCE_CRITERIA)

```json
{
  "type": "ACCEPTANCE_CRITERIA",
  "acceptanceCriteria": ["标准1", "标准2"],
  "totalCriteria": 2
}
```

### 风险识别 (RISK)

```json
{
  "type": "RISK",
  "risks": [
    {"level": "HIGH", "description": "风险描述"}
  ],
  "totalRisks": 1,
  "highRiskCount": 1
}
```

### 澄清问题 (QUESTIONS)

```json
{
  "type": "QUESTIONS",
  "questions": [
    {"question": "问题内容", "severity": "HIGH"}
  ],
  "totalQuestions": 1,
  "highSeverityCount": 1
}
```

### 任务拆解 (TASK_BREAKDOWN)

```json
{
  "type": "TASK_BREAKDOWN",
  "tasks": [
    {"task": "任务名称", "priority": "P1", "estimatedHours": "2"}
  ],
  "totalTasks": 1,
  "totalEstimatedHours": 2
}
```

## 6. 前后端协作说明

前端页面通过 Fetch API 调用后端接口：

1. **列表页** (`index.html`)
   - 调用 `GET /api/work-items` 获取列表
   - 调用 `DELETE /api/work-items/{id}` 删除工作项

2. **详情页** (`detail.html`)
   - 调用 `GET /api/work-items/{id}` 获取详情
   - 调用 `POST /api/work-items/{id}/transition` 执行流转
   - 调用 `GET /api/work-items/{id}/allowed-transitions` 获取可选状态
   - 调用澄清问题 API 管理澄清问题
   - 调用 AI 分析 API 触发分析

## 7. 后续扩展

1. **认证授权**: 添加 JWT Token 验证
2. **分页支持**: 列表接口添加 page、size 参数
3. **批量操作**: 支持批量状态流转
4. **WebSocket**: 实时推送状态变更通知
5. **导出功能**: 支持导出工作项为 Excel/CSV
