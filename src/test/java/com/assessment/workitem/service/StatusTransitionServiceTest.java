package com.assessment.workitem.service;

import com.assessment.workitem.exception.BusinessException;
import com.assessment.workitem.model.entity.ClarificationQuestion;
import com.assessment.workitem.model.entity.StatusHistory;
import com.assessment.workitem.model.entity.WorkItem;
import com.assessment.workitem.model.enums.*;
import com.assessment.workitem.repository.ClarificationRepository;
import com.assessment.workitem.repository.StatusHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 状态流转服务测试
 */
@ExtendWith(MockitoExtension.class)
class StatusTransitionServiceTest {

    @Mock
    private ClarificationRepository clarificationRepository;

    @Mock
    private StatusHistoryRepository statusHistoryRepository;

    @InjectMocks
    private StatusTransitionService statusTransitionService;

    private WorkItem workItem;

    @BeforeEach
    void setUp() {
        workItem = new WorkItem();
        workItem.setId(1L);
        workItem.setTitle("测试工作项");
        workItem.setType(WorkItemType.STORY);
        workItem.setPriority(Priority.P1);
        workItem.setStatus(WorkItemStatus.DRAFT);
    }

    @Test
    @DisplayName("合法流转: DRAFT → ANALYZING")
    void transition_draftToAnalyzing_shouldSucceed() {
        // given
        when(statusHistoryRepository.save(any(StatusHistory.class))).thenAnswer(i -> i.getArgument(0));

        // when
        StatusHistory history = statusTransitionService.transition(workItem, WorkItemStatus.ANALYZING, "开始分析", "candidate");

        // then
        assertEquals(WorkItemStatus.DRAFT, history.getFromStatus());
        assertEquals(WorkItemStatus.ANALYZING, history.getToStatus());
        assertEquals(WorkItemStatus.ANALYZING, workItem.getStatus());
    }

    @Test
    @DisplayName("合法流转: ANALYZING → READY")
    void transition_analyzingToReady_shouldSucceed() {
        // given
        workItem.setStatus(WorkItemStatus.ANALYZING);
        when(clarificationRepository.countByWorkItemIdAndSeverityAndStatus(
                eq(1L), eq(Severity.HIGH), eq(QuestionStatus.UNRESOLVED))).thenReturn(0L);
        when(statusHistoryRepository.save(any(StatusHistory.class))).thenAnswer(i -> i.getArgument(0));

        // when
        StatusHistory history = statusTransitionService.transition(workItem, WorkItemStatus.READY, "分析完成", "candidate");

        // then
        assertEquals(WorkItemStatus.ANALYZING, history.getFromStatus());
        assertEquals(WorkItemStatus.READY, history.getToStatus());
        assertEquals(WorkItemStatus.READY, workItem.getStatus());
    }

    @Test
    @DisplayName("合法流转: READY → IN_PROGRESS → TESTING → COMPLETED")
    void transition_readyToCompleted_shouldSucceed() {
        // given
        when(clarificationRepository.countByWorkItemIdAndSeverityAndStatus(
                any(), any(), any())).thenReturn(0L);
        when(statusHistoryRepository.save(any(StatusHistory.class))).thenAnswer(i -> i.getArgument(0));

        // READY → IN_PROGRESS
        workItem.setStatus(WorkItemStatus.READY);
        statusTransitionService.transition(workItem, WorkItemStatus.IN_PROGRESS, "开始开发", "candidate");
        assertEquals(WorkItemStatus.IN_PROGRESS, workItem.getStatus());

        // IN_PROGRESS → TESTING
        statusTransitionService.transition(workItem, WorkItemStatus.TESTING, "开发完成", "candidate");
        assertEquals(WorkItemStatus.TESTING, workItem.getStatus());

        // TESTING → COMPLETED
        statusTransitionService.transition(workItem, WorkItemStatus.COMPLETED, "测试通过", "candidate");
        assertEquals(WorkItemStatus.COMPLETED, workItem.getStatus());
    }

    @Test
    @DisplayName("非法流转: DRAFT → IN_PROGRESS 应抛出异常")
    void transition_draftToInProgress_shouldThrowException() {
        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                statusTransitionService.transition(workItem, WorkItemStatus.IN_PROGRESS, "非法流转", "candidate"));

        assertTrue(exception.getMessage().contains("非法的状态流转"));
    }

    @Test
    @DisplayName("非法流转: COMPLETED → DRAFT 应抛出异常")
    void transition_completedToDraft_shouldThrowException() {
        // given
        workItem.setStatus(WorkItemStatus.COMPLETED);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                statusTransitionService.transition(workItem, WorkItemStatus.DRAFT, "非法流转", "candidate"));

        assertTrue(exception.getMessage().contains("非法的状态流转"));
    }

    @Test
    @DisplayName("相同状态流转应抛出异常")
    void transition_sameStatus_shouldThrowException() {
        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                statusTransitionService.transition(workItem, WorkItemStatus.DRAFT, "相同状态", "candidate"));

        assertTrue(exception.getMessage().contains("目标状态与当前状态相同"));
    }

    @Test
    @DisplayName("存在未解决高优先级澄清问题时，应阻止流转到 READY")
    void transition_withUnresolvedHighSeverityClarification_shouldBlock() {
        // given
        workItem.setStatus(WorkItemStatus.ANALYZING);
        when(clarificationRepository.countByWorkItemIdAndSeverityAndStatus(
                eq(1L), eq(Severity.HIGH), eq(QuestionStatus.UNRESOLVED))).thenReturn(1L);
        when(clarificationRepository.findByWorkItemIdAndSeverityAndStatus(
                eq(1L), eq(Severity.HIGH), eq(QuestionStatus.UNRESOLVED))).thenReturn(List.of(
                createClarification(1L, "需求不明确", Severity.HIGH, QuestionStatus.UNRESOLVED)
        ));

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () ->
                statusTransitionService.transition(workItem, WorkItemStatus.READY, "尝试流转", "candidate"));

        assertTrue(exception.getMessage().contains("未解决的高优先级澄清问题"));
        assertEquals(WorkItemStatus.ANALYZING, workItem.getStatus()); // 状态应保持不变
    }

    @Test
    @DisplayName("解决高优先级澄清问题后，应允许流转到 READY")
    void transition_afterResolvingClarification_shouldSucceed() {
        // given
        workItem.setStatus(WorkItemStatus.ANALYZING);
        when(clarificationRepository.countByWorkItemIdAndSeverityAndStatus(
                eq(1L), eq(Severity.HIGH), eq(QuestionStatus.UNRESOLVED))).thenReturn(0L);
        when(statusHistoryRepository.save(any(StatusHistory.class))).thenAnswer(i -> i.getArgument(0));

        // when
        StatusHistory history = statusTransitionService.transition(workItem, WorkItemStatus.READY, "问题已解决", "candidate");

        // then
        assertEquals(WorkItemStatus.READY, workItem.getStatus());
    }

    @Test
    @DisplayName("退回: TESTING → IN_PROGRESS 应该成功")
    void transition_testingToInProgress_shouldSucceed() {
        // given
        workItem.setStatus(WorkItemStatus.TESTING);
        when(statusHistoryRepository.save(any(StatusHistory.class))).thenAnswer(i -> i.getArgument(0));

        // when
        StatusHistory history = statusTransitionService.transition(workItem, WorkItemStatus.IN_PROGRESS, "测试不通过", "candidate");

        // then
        assertEquals(WorkItemStatus.TESTING, history.getFromStatus());
        assertEquals(WorkItemStatus.IN_PROGRESS, history.getToStatus());
    }

    @Test
    @DisplayName("获取状态历史应返回正确数据")
    void getHistory_shouldReturnCorrectData() {
        // given
        List<StatusHistory> expectedHistory = List.of(
                createStatusHistory(1L, WorkItemStatus.DRAFT, WorkItemStatus.ANALYZING),
                createStatusHistory(2L, WorkItemStatus.ANALYZING, WorkItemStatus.READY)
        );
        when(statusHistoryRepository.findByWorkItemIdOrderByChangedAtDesc(1L)).thenReturn(expectedHistory);

        // when
        List<StatusHistory> history = statusTransitionService.getHistory(1L);

        // then
        assertEquals(2, history.size());
    }

    @Test
    @DisplayName("获取允许的流转状态应返回正确的状态集合")
    void getAllowedTransitions_shouldReturnCorrectStates() {
        // DRAFT
        assertEquals(Set.of(WorkItemStatus.ANALYZING), statusTransitionService.getAllowedTransitions(WorkItemStatus.DRAFT));

        // ANALYZING
        assertEquals(Set.of(WorkItemStatus.DRAFT, WorkItemStatus.READY), statusTransitionService.getAllowedTransitions(WorkItemStatus.ANALYZING));

        // COMPLETED
        assertEquals(Set.of(), statusTransitionService.getAllowedTransitions(WorkItemStatus.COMPLETED));
    }

    private ClarificationQuestion createClarification(Long id, String content, Severity severity, QuestionStatus status) {
        ClarificationQuestion question = new ClarificationQuestion();
        question.setId(id);
        question.setContent(content);
        question.setSeverity(severity);
        question.setStatus(status);
        return question;
    }

    private StatusHistory createStatusHistory(Long id, WorkItemStatus from, WorkItemStatus to) {
        StatusHistory history = new StatusHistory();
        history.setId(id);
        history.setFromStatus(from);
        history.setToStatus(to);
        return history;
    }
}
