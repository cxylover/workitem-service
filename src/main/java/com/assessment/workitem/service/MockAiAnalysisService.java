package com.assessment.workitem.service;

import com.assessment.workitem.model.entity.AiAnalysisResult;
import com.assessment.workitem.model.entity.WorkItem;
import com.assessment.workitem.model.enums.AnalysisType;
import com.assessment.workitem.repository.AiAnalysisResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Mock AI 分析服务实现
 * 使用规则和关键词提取生成结构化分析结果
 *
 * 替换为真实 LLM 实现时，只需实现 AiAnalysisService 接口并注入即可
 */
@Service
public class MockAiAnalysisService implements AiAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(MockAiAnalysisService.class);

    private final AiAnalysisResultRepository analysisResultRepository;
    private final ObjectMapper objectMapper;

    public MockAiAnalysisService(AiAnalysisResultRepository analysisResultRepository,
                                 ObjectMapper objectMapper) {
        this.analysisResultRepository = analysisResultRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public AiAnalysisResult analyze(WorkItem workItem, AnalysisType type) {
        String result = switch (type) {
            case SUMMARY -> generateSummary(workItem);
            case ACCEPTANCE_CRITERIA -> generateAcceptanceCriteria(workItem);
            case RISK -> identifyRisks(workItem);
            case QUESTIONS -> generateQuestions(workItem);
            case TASK_BREAKDOWN -> generateTaskBreakdown(workItem);
        };

        AiAnalysisResult analysisResult = AiAnalysisResult.builder()
                .workItem(workItem)
                .analysisType(type)
                .result(result)
                .build();

        AiAnalysisResult saved = analysisResultRepository.save(analysisResult);
        log.info("为工作项 [{}] 生成 {} 分析结果", workItem.getId(), type);
        return saved;
    }

    @Override
    public List<AiAnalysisResult> getAnalysisHistory(Long workItemId) {
        return analysisResultRepository.findByWorkItemId(workItemId);
    }

    /**
     * 生成需求摘要
     */
    private String generateSummary(WorkItem workItem) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", "SUMMARY");
        result.put("title", workItem.getTitle());

        String description = workItem.getDescription() != null ? workItem.getDescription() : "";
        String summary = extractKeyInfo(description);
        result.put("summary", summary);

        result.put("workItemType", workItem.getType().name());
        result.put("priority", workItem.getPriority().name());

        return toJson(result);
    }

    /**
     * 生成验收标准建议
     */
    private String generateAcceptanceCriteria(WorkItem workItem) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", "ACCEPTANCE_CRITERIA");

        List<String> criteria = new ArrayList<>();
        String desc = workItem.getDescription() != null ? workItem.getDescription().toLowerCase() : "";

        // 基于关键词生成验收标准
        if (desc.contains("状态") || desc.contains("流转")) {
            criteria.add("状态流转应按照预定义的规则进行，非法流转应被拦截");
            criteria.add("状态变更应记录历史，包含变更时间、操作人和原因");
        }
        if (desc.contains("创建") || desc.contains("新增")) {
            criteria.add("创建操作应成功保存所有必填字段");
            criteria.add("创建后应返回完整的资源信息");
        }
        if (desc.contains("查询") || desc.contains("列表")) {
            criteria.add("查询结果应支持分页和筛选");
            criteria.add("查询响应时间应在合理范围内");
        }
        if (desc.contains("澄清") || desc.contains("问题")) {
            criteria.add("澄清问题应支持新增、查询和解决操作");
            criteria.add("未解决的高优先级问题应能阻断状态流转");
        }
        if (desc.contains("ai") || desc.contains("分析")) {
            criteria.add("AI 分析结果应以结构化格式返回");
            criteria.add("分析结果应包含可操作的建议");
        }

        // 如果没有匹配到关键词，提供通用验收标准
        if (criteria.isEmpty()) {
            criteria.add("功能应按需求描述正确实现");
            criteria.add("异常情况应有明确的错误提示");
            criteria.add("核心业务规则应被正确执行");
        }

        result.put("acceptanceCriteria", criteria);
        result.put("totalCriteria", criteria.size());

        return toJson(result);
    }

    /**
     * 识别风险点
     */
    private String identifyRisks(WorkItem workItem) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", "RISK");

        List<Map<String, String>> risks = new ArrayList<>();
        String desc = workItem.getDescription() != null ? workItem.getDescription().toLowerCase() : "";

        // 基于关键词识别风险
        if (desc.contains("并发") || desc.contains("多人")) {
            risks.add(Map.of("level", "HIGH", "description", "多人并发操作可能导致数据冲突，建议实现乐观锁或分布式锁"));
        }
        if (desc.contains("状态") || desc.contains("流转")) {
            risks.add(Map.of("level", "MEDIUM", "description", "状态流转规则复杂，可能存在边界条件未覆盖"));
        }
        if (desc.contains("外部") || desc.contains("第三方") || desc.contains("api")) {
            risks.add(Map.of("level", "HIGH", "description", "依赖外部服务，需要考虑超时、重试和降级策略"));
        }
        if (desc.contains("大量") || desc.contains("批量")) {
            risks.add(Map.of("level", "MEDIUM", "description", "大批量数据处理可能导致性能问题，建议分批处理"));
        }
        if (desc.contains("权限") || desc.contains("认证")) {
            risks.add(Map.of("level", "HIGH", "description", "涉及权限控制，需要确保安全性，防止越权访问"));
        }

        // 如果没有识别到风险，提供通用风险提示
        if (risks.isEmpty()) {
            risks.add(Map.of("level", "LOW", "description", "需求描述较简单，可能存在理解偏差，建议与需求方确认"));
        }

        result.put("risks", risks);
        result.put("totalRisks", risks.size());
        result.put("highRiskCount", risks.stream().filter(r -> "HIGH".equals(r.get("level"))).count());

        return toJson(result);
    }

    /**
     * 生成澄清问题
     */
    private String generateQuestions(WorkItem workItem) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", "QUESTIONS");

        List<Map<String, String>> questions = new ArrayList<>();
        String desc = workItem.getDescription() != null ? workItem.getDescription().toLowerCase() : "";

        // 基于关键词生成澄清问题
        if (desc.contains("状态") || desc.contains("流转")) {
            questions.add(Map.of("question", "状态流转的完整规则是什么？哪些状态可以退回？", "severity", "HIGH"));
            questions.add(Map.of("question", "完成状态是否允许再次变更？", "severity", "MEDIUM"));
        }
        if (desc.contains("创建") || desc.contains("新增")) {
            questions.add(Map.of("question", "哪些字段是必填的？是否有字段长度或格式限制？", "severity", "MEDIUM"));
        }
        if (desc.contains("查询") || desc.contains("列表")) {
            questions.add(Map.of("question", "是否需要分页？默认每页显示多少条？", "severity", "LOW"));
            questions.add(Map.of("question", "支持哪些筛选条件？是否支持模糊搜索？", "severity", "MEDIUM"));
        }
        if (desc.contains("ai") || desc.contains("分析")) {
            questions.add(Map.of("question", "AI 分析的触发时机是什么？是自动还是手动？", "severity", "MEDIUM"));
            questions.add(Map.of("question", "AI 分析结果的格式和内容要求是什么？", "severity", "HIGH"));
        }
        if (desc.contains("用户") || desc.contains("负责人")) {
            questions.add(Map.of("question", "用户体系如何设计？是否需要登录认证？", "severity", "HIGH"));
        }

        // 如果没有匹配到关键词，提供通用澄清问题
        if (questions.isEmpty()) {
            questions.add(Map.of("question", "需求的验收标准是什么？", "severity", "HIGH"));
            questions.add(Map.of("question", "异常场景如何处理？", "severity", "MEDIUM"));
            questions.add(Map.of("question", "是否需要考虑性能要求？", "severity", "LOW"));
        }

        result.put("questions", questions);
        result.put("totalQuestions", questions.size());
        result.put("highSeverityCount", questions.stream().filter(q -> "HIGH".equals(q.get("severity"))).count());

        return toJson(result);
    }

    /**
     * 生成任务拆解建议
     */
    private String generateTaskBreakdown(WorkItem workItem) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", "TASK_BREAKDOWN");

        List<Map<String, String>> tasks = new ArrayList<>();
        String desc = workItem.getDescription() != null ? workItem.getDescription().toLowerCase() : "";

        // 基于关键词生成任务拆解
        if (desc.contains("api") || desc.contains("接口")) {
            tasks.add(Map.of("task", "设计 API 接口", "priority", "P1", "estimatedHours", "2"));
            tasks.add(Map.of("task", "实现 API 接口", "priority", "P1", "estimatedHours", "4"));
            tasks.add(Map.of("task", "编写 API 测试", "priority", "P2", "estimatedHours", "2"));
        }
        if (desc.contains("数据库") || desc.contains("存储")) {
            tasks.add(Map.of("task", "设计数据模型", "priority", "P1", "estimatedHours", "2"));
            tasks.add(Map.of("task", "实现数据访问层", "priority", "P1", "estimatedHours", "3"));
        }
        if (desc.contains("前端") || desc.contains("页面")) {
            tasks.add(Map.of("task", "设计页面原型", "priority", "P2", "estimatedHours", "2"));
            tasks.add(Map.of("task", "实现前端页面", "priority", "P2", "estimatedHours", "4"));
        }
        if (desc.contains("测试")) {
            tasks.add(Map.of("task", "编写单元测试", "priority", "P2", "estimatedHours", "3"));
            tasks.add(Map.of("task", "编写集成测试", "priority", "P3", "estimatedHours", "2"));
        }

        // 如果没有匹配到关键词，提供通用任务拆解
        if (tasks.isEmpty()) {
            tasks.add(Map.of("task", "需求分析与设计", "priority", "P1", "estimatedHours", "2"));
            tasks.add(Map.of("task", "核心功能实现", "priority", "P1", "estimatedHours", "4"));
            tasks.add(Map.of("task", "测试与验证", "priority", "P2", "estimatedHours", "2"));
            tasks.add(Map.of("task", "文档编写", "priority", "P3", "estimatedHours", "1"));
        }

        result.put("tasks", tasks);
        result.put("totalTasks", tasks.size());
        result.put("totalEstimatedHours", tasks.stream()
                .mapToInt(t -> Integer.parseInt(t.get("estimatedHours")))
                .sum());

        return toJson(result);
    }

    /**
     * 提取关键信息生成摘要
     */
    private String extractKeyInfo(String description) {
        if (description == null || description.isEmpty()) {
            return "需求描述为空，建议补充详细描述。";
        }

        // 简单的关键信息提取
        StringBuilder summary = new StringBuilder();
        String[] sentences = description.split("[。.！!？?]");
        int count = 0;
        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (!trimmed.isEmpty() && count < 3) {
                summary.append(trimmed).append("。");
                count++;
            }
        }

        return summary.length() > 0 ? summary.toString() : description.substring(0, Math.min(100, description.length())) + "...";
    }

    /**
     * 转换为 JSON 字符串
     */
    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JSON 转换失败", e);
            return "{}";
        }
    }
}
