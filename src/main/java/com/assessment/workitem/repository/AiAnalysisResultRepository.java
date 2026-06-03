package com.assessment.workitem.repository;

import com.assessment.workitem.model.entity.AiAnalysisResult;
import com.assessment.workitem.model.enums.AnalysisType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI 分析结果 Repository
 */
@Repository
public interface AiAnalysisResultRepository extends JpaRepository<AiAnalysisResult, Long> {

    /** 按工作项查询 */
    List<AiAnalysisResult> findByWorkItemId(Long workItemId);

    /** 按工作项和分析类型查询 */
    List<AiAnalysisResult> findByWorkItemIdAndAnalysisType(Long workItemId, AnalysisType analysisType);
}
