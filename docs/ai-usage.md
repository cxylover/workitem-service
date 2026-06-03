# AI 使用说明

## 1. 使用的 AI 工具

| 工具 | 用途 |
|------|------|
| Claude Code | 代码生成、架构设计、文档编写 |

## 2. 使用场景

| 阶段 | 是否使用 AI | 说明 |
|------|------------|------|
| 需求理解 | ✅ | 使用 AI 分析题目要求，拆解功能点 |
| 任务拆解 | ✅ | 使用 AI 生成任务列表和实现顺序 |
| 方案设计 | ✅ | 使用 AI 设计状态机、API 结构、数据库模型 |
| 代码生成 | ✅ | 使用 AI 生成大部分代码框架和实现 |
| 测试生成 | ✅ | 使用 AI 生成单元测试代码 |
| Bug 修复 | ❌ | 无 Bug 需要修复 |
| 文档编写 | ✅ | 使用 AI 生成 README、API 文档等 |

## 3. 关键 Prompt / Skill 摘要

1. **项目初始化**: "创建一个 Spring Boot 3 项目，包含工作项管理、状态流转、澄清问题、AI 分析功能"
2. **状态机设计**: "设计工作项状态流转规则，包含阻断规则"
3. **API 设计**: "设计 RESTful API，支持工作项 CRUD、状态流转、澄清问题管理"
4. **测试生成**: "为状态流转服务编写单元测试，覆盖合法流转、非法流转、阻断规则"

## 4. AI 生成内容

以下内容由 AI 辅助生成：

1. **项目结构** - Maven 项目配置、包结构
2. **实体类** - WorkItem、ClarificationQuestion、StatusHistory、AiAnalysisResult
3. **枚举类** - WorkItemStatus、WorkItemType、Priority 等
4. **服务层** - StatusTransitionService、WorkItemService、ClarificationService
5. **控制器** - WorkItemController、ClarificationController、AiAnalysisController
6. **前端页面** - index.html、detail.html、api.js、style.css
7. **单元测试** - StatusTransitionServiceTest、ClarificationServiceTest、MockAiAnalysisServiceTest
8. **文档** - README.md、api-design-proposal.md、test-plan.md

## 5. 人工修正内容

AI 生成后进行的人工调整：

1. **种子数据** - 手动调整 data.sql 中的初始数据，确保与题目提供的样例一致
2. **前端样式** - 微调部分 CSS 样式，提升用户体验
3. **错误信息** - 优化部分错误提示信息，使其更友好

## 6. 效果评价

### AI 帮助明显的地方

1. **快速搭建项目框架** - AI 能够快速生成完整的项目结构和配置
2. **代码生成效率高** - 大部分业务代码由 AI 生成，节省大量时间
3. **测试覆盖全面** - AI 生成的测试用例覆盖了主要场景
4. **文档质量好** - 生成的文档结构清晰，内容完整

### 效果不好的地方

1. **业务逻辑细节** - AI 生成的阻断规则实现需要人工验证和调整
2. **前端交互** - 生成的前端代码功能完整，但用户体验需要优化
3. **种子数据** - 需要手动调整以匹配题目要求

### 遇到的问题

1. AI 生成的代码需要仔细审查，确保符合业务需求
2. 部分边界条件需要手动补充测试
3. 前端代码的错误处理需要完善
