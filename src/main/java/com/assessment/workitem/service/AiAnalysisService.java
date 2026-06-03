package com.assessment.workitem.service;

import com.assessment.workitem.model.entity.AiAnalysisResult;
import com.assessment.workitem.model.entity.WorkItem;
import com.assessment.workitem.model.enums.AnalysisType;

import java.util.List;

/**
 * AI 分析服务接口
 * 定义 AI 辅助分析能力，便于后续替换为真实 LLM 实现
 */
public interface AiAnalysisService {

    /**
     * 执行 AI 分析
     *
     * @param workItem 工作项
     * @param type     分析类型
     * @return 分析结果
     */
    AiAnalysisResult analyze(WorkItem workItem, AnalysisType type);

    /**
     * 获取工作项的分析历史
     *
     * @param workItemId 工作项 ID
     * @return 分析结果列表
     */
    List<AiAnalysisResult> getAnalysisHistory(Long workItemId);
}
