package com.assessment.workitem.service;

import com.assessment.workitem.exception.BusinessException;
import com.assessment.workitem.model.entity.ClarificationQuestion;
import com.assessment.workitem.model.entity.StatusHistory;
import com.assessment.workitem.model.entity.WorkItem;
import com.assessment.workitem.model.enums.QuestionStatus;
import com.assessment.workitem.model.enums.Severity;
import com.assessment.workitem.model.enums.WorkItemStatus;
import com.assessment.workitem.repository.ClarificationRepository;
import com.assessment.workitem.repository.StatusHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 状态流转服务
 * 负责验证和执行工作项的状态变更
 */
@Service
public class StatusTransitionService {

    private static final Logger log = LoggerFactory.getLogger(StatusTransitionService.class);

    private final ClarificationRepository clarificationRepository;
    private final StatusHistoryRepository statusHistoryRepository;

    public StatusTransitionService(ClarificationRepository clarificationRepository,
                                   StatusHistoryRepository statusHistoryRepository) {
        this.clarificationRepository = clarificationRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    /**
     * 合法的状态流转规则
     * Key: 当前状态, Value: 可以流转到的目标状态集合
     */
    private static final Map<WorkItemStatus, Set<WorkItemStatus>> TRANSITION_RULES = new EnumMap<>(WorkItemStatus.class);

    static {
        TRANSITION_RULES.put(WorkItemStatus.DRAFT, Set.of(WorkItemStatus.ANALYZING));
        TRANSITION_RULES.put(WorkItemStatus.ANALYZING, Set.of(WorkItemStatus.DRAFT, WorkItemStatus.READY));
        TRANSITION_RULES.put(WorkItemStatus.READY, Set.of(WorkItemStatus.ANALYZING, WorkItemStatus.IN_PROGRESS));
        TRANSITION_RULES.put(WorkItemStatus.IN_PROGRESS, Set.of(WorkItemStatus.READY, WorkItemStatus.TESTING));
        TRANSITION_RULES.put(WorkItemStatus.TESTING, Set.of(WorkItemStatus.IN_PROGRESS, WorkItemStatus.COMPLETED));
        TRANSITION_RULES.put(WorkItemStatus.COMPLETED, Set.of()); // 终态，不可变更
    }

    /**
     * 需要检查澄清问题阻断的目标状态
     * 进入这些状态时，需要检查是否存在未解决的高优先级澄清问题
     */
    private static final Set<WorkItemStatus> BLOCKED_BY_CLARIFICATIONS = Set.of(
            WorkItemStatus.READY,
            WorkItemStatus.IN_PROGRESS,
            WorkItemStatus.TESTING,
            WorkItemStatus.COMPLETED
    );

    /**
     * 验证并执行状态流转
     *
     * @param workItem     工作项
     * @param targetStatus 目标状态
     * @param reason       流转原因
     * @param changedBy    操作人
     * @return 状态历史记录
     */
    @Transactional
    public StatusHistory transition(WorkItem workItem, WorkItemStatus targetStatus, String reason, String changedBy) {
        WorkItemStatus currentStatus = workItem.getStatus();

        // 1. 验证目标状态是否与当前状态相同
        if (currentStatus == targetStatus) {
            throw new BusinessException("目标状态与当前状态相同，无需流转");
        }

        // 2. 验证流转是否合法
        validateTransition(currentStatus, targetStatus);

        // 3. 检查澄清问题阻断规则
        if (BLOCKED_BY_CLARIFICATIONS.contains(targetStatus)) {
            checkClarificationBlocker(workItem.getId());
        }

        // 4. 执行状态变更
        workItem.setStatus(targetStatus);

        // 5. 记录状态历史
        StatusHistory history = StatusHistory.builder()
                .workItem(workItem)
                .fromStatus(currentStatus)
                .toStatus(targetStatus)
                .reason(reason)
                .changedBy(changedBy)
                .build();

        statusHistoryRepository.save(history);
        log.info("工作项 [{}] 状态从 {} 流转到 {}", workItem.getId(), currentStatus, targetStatus);

        return history;
    }

    /**
     * 验证状态流转是否合法
     */
    private void validateTransition(WorkItemStatus current, WorkItemStatus target) {
        Set<WorkItemStatus> allowedTargets = TRANSITION_RULES.get(current);

        if (allowedTargets == null || !allowedTargets.contains(target)) {
            throw new BusinessException(
                    String.format("非法的状态流转: %s → %s。允许的流转: %s",
                            current, target,
                            allowedTargets != null ? allowedTargets : "无"));
        }
    }

    /**
     * 检查澄清问题阻断规则
     * 如果存在未解决的高优先级澄清问题，阻止状态流转
     */
    private void checkClarificationBlocker(Long workItemId) {
        long count = clarificationRepository.countByWorkItemIdAndSeverityAndStatus(
                workItemId, Severity.HIGH, QuestionStatus.UNRESOLVED);

        if (count > 0) {
            List<ClarificationQuestion> blockers = clarificationRepository
                    .findByWorkItemIdAndSeverityAndStatus(workItemId, Severity.HIGH, QuestionStatus.UNRESOLVED);

            StringJoiner joiner = new StringJoiner("、");
            blockers.forEach(q -> joiner.add(q.getContent()));

            throw new BusinessException(
                    String.format("存在 %d 个未解决的高优先级澄清问题，无法继续流转。问题: %s",
                            count, joiner));
        }
    }

    /**
     * 获取工作项的状态历史
     */
    public List<StatusHistory> getHistory(Long workItemId) {
        return statusHistoryRepository.findByWorkItemIdOrderByChangedAtDesc(workItemId);
    }

    /**
     * 获取当前状态可以流转到的目标状态列表
     */
    public Set<WorkItemStatus> getAllowedTransitions(WorkItemStatus currentStatus) {
        return TRANSITION_RULES.getOrDefault(currentStatus, Set.of());
    }
}
