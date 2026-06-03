package com.assessment.workitem.service;

import com.assessment.workitem.dto.ClarificationCreateRequest;
import com.assessment.workitem.dto.ClarificationUpdateRequest;
import com.assessment.workitem.exception.ResourceNotFoundException;
import com.assessment.workitem.model.entity.ClarificationQuestion;
import com.assessment.workitem.model.entity.WorkItem;
import com.assessment.workitem.model.enums.*;
import com.assessment.workitem.repository.ClarificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 澄清问题服务测试
 */
@ExtendWith(MockitoExtension.class)
class ClarificationServiceTest {

    @Mock
    private ClarificationRepository clarificationRepository;

    @Mock
    private IWorkItemService workItemService;

    private ClarificationService clarificationService;

    private WorkItem workItem;

    @BeforeEach
    void setUp() {
        clarificationService = new ClarificationService(clarificationRepository, workItemService);

        workItem = new WorkItem();
        workItem.setId(1L);
        workItem.setTitle("测试工作项");
        workItem.setType(WorkItemType.STORY);
        workItem.setPriority(Priority.P1);
        workItem.setStatus(WorkItemStatus.DRAFT);
    }

    @Test
    @DisplayName("创建澄清问题应成功")
    void create_shouldSucceed() {
        // given
        ClarificationCreateRequest request = new ClarificationCreateRequest();
        request.setContent("需求不明确");
        request.setSeverity(Severity.HIGH);

        when(workItemService.getById(1L)).thenReturn(workItem);
        when(clarificationRepository.save(any(ClarificationQuestion.class))).thenAnswer(i -> {
            ClarificationQuestion q = i.getArgument(0);
            q.setId(1L);
            return q;
        });

        // when
        ClarificationQuestion result = clarificationService.create(1L, request);

        // then
        assertNotNull(result);
        assertEquals("需求不明确", result.getContent());
        assertEquals(Severity.HIGH, result.getSeverity());
        assertEquals(QuestionStatus.UNRESOLVED, result.getStatus());
        assertEquals(workItem, result.getWorkItem());
    }

    @Test
    @DisplayName("解决澄清问题应设置解决时间和状态")
    void resolve_shouldSetResolvedTimeAndStatus() {
        // given
        ClarificationQuestion question = new ClarificationQuestion();
        question.setId(1L);
        question.setWorkItem(workItem);
        question.setContent("需求不明确");
        question.setSeverity(Severity.HIGH);
        question.setStatus(QuestionStatus.UNRESOLVED);

        ClarificationUpdateRequest request = new ClarificationUpdateRequest();
        request.setStatus(QuestionStatus.RESOLVED);
        request.setAnswer("已确认需求范围");

        when(clarificationRepository.findById(1L)).thenReturn(Optional.of(question));
        when(clarificationRepository.save(any(ClarificationQuestion.class))).thenAnswer(i -> i.getArgument(0));

        // when
        ClarificationQuestion result = clarificationService.update(1L, request);

        // then
        assertEquals(QuestionStatus.RESOLVED, result.getStatus());
        assertEquals("已确认需求范围", result.getAnswer());
        assertNotNull(result.getResolvedAt());
    }

    @Test
    @DisplayName("获取不存在的澄清问题应抛出异常")
    void getById_notFound_shouldThrowException() {
        // given
        when(clarificationRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () ->
                clarificationService.getById(999L));
    }

    @Test
    @DisplayName("获取工作项下的澄清问题列表应返回正确数据")
    void listByWorkItem_shouldReturnCorrectData() {
        // given
        List<ClarificationQuestion> questions = List.of(
                createClarification(1L, "问题1", Severity.HIGH, QuestionStatus.UNRESOLVED),
                createClarification(2L, "问题2", Severity.MEDIUM, QuestionStatus.RESOLVED)
        );
        when(clarificationRepository.findByWorkItemId(1L)).thenReturn(questions);

        // when
        List<ClarificationQuestion> result = clarificationService.listByWorkItem(1L);

        // then
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取未解决的澄清问题应返回正确数据")
    void listUnresolved_shouldReturnOnlyUnresolved() {
        // given
        List<ClarificationQuestion> questions = List.of(
                createClarification(1L, "问题1", Severity.HIGH, QuestionStatus.UNRESOLVED)
        );
        when(clarificationRepository.findByWorkItemIdAndStatus(1L, QuestionStatus.UNRESOLVED)).thenReturn(questions);

        // when
        List<ClarificationQuestion> result = clarificationService.listUnresolved(1L);

        // then
        assertEquals(1, result.size());
        assertEquals(QuestionStatus.UNRESOLVED, result.get(0).getStatus());
    }

    private ClarificationQuestion createClarification(Long id, String content, Severity severity, QuestionStatus status) {
        ClarificationQuestion question = new ClarificationQuestion();
        question.setId(id);
        question.setContent(content);
        question.setSeverity(severity);
        question.setStatus(status);
        return question;
    }
}
