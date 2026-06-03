package com.assessment.workitem.repository;

import com.assessment.workitem.model.entity.ClarificationQuestion;
import com.assessment.workitem.model.enums.QuestionStatus;
import com.assessment.workitem.model.enums.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 澄清问题 Repository
 */
@Repository
public interface ClarificationRepository extends JpaRepository<ClarificationQuestion, Long> {

    /** 按工作项查询 */
    List<ClarificationQuestion> findByWorkItemId(Long workItemId);

    /** 按工作项和状态查询 */
    List<ClarificationQuestion> findByWorkItemIdAndStatus(Long workItemId, QuestionStatus status);

    /** 按工作项和严重程度查询 */
    List<ClarificationQuestion> findByWorkItemIdAndSeverity(Long workItemId, Severity severity);

    /** 按工作项、严重程度和状态查询 */
    List<ClarificationQuestion> findByWorkItemIdAndSeverityAndStatus(
            Long workItemId, Severity severity, QuestionStatus status);

    /** 统计工作项下未解决的高优先级问题数量 */
    long countByWorkItemIdAndSeverityAndStatus(
            Long workItemId, Severity severity, QuestionStatus status);
}
