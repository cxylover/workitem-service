package com.assessment.workitem.service;

import com.assessment.workitem.model.entity.AiAnalysisResult;
import com.assessment.workitem.model.entity.WorkItem;
import com.assessment.workitem.model.enums.*;
import com.assessment.workitem.repository.AiAnalysisResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Mock AI 分析服务测试
 */
@ExtendWith(MockitoExtension.class)
class MockAiAnalysisServiceTest {

    @Mock
    private AiAnalysisResultRepository analysisResultRepository;

    private ObjectMapper objectMapper;

    private MockAiAnalysisService mockAiAnalysisService;

    private WorkItem workItem;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockAiAnalysisService = new MockAiAnalysisService(analysisResultRepository, objectMapper);

        workItem = new WorkItem();
        workItem.setId(1L);
        workItem.setTitle("支持工作项状态流转");
        workItem.setDescription("作为研发负责人，我希望工作项能够按照分析、准备、开发、测试和完成等阶段进行流转");
        workItem.setType(WorkItemType.STORY);
        workItem.setPriority(Priority.P1);
        workItem.setStatus(WorkItemStatus.DRAFT);
    }

    @Test
    @DisplayName("生成需求摘要应返回结构化结果")
    void analyze_summary_shouldReturnStructuredResult() {
        // given
        when(analysisResultRepository.save(any(AiAnalysisResult.class))).thenAnswer(i -> {
            AiAnalysisResult r = i.getArgument(0);
            r.setId(1L);
            return r;
        });

        // when
        AiAnalysisResult result = mockAiAnalysisService.analyze(workItem, AnalysisType.SUMMARY);

        // then
        assertNotNull(result);
        assertEquals(AnalysisType.SUMMARY, result.getAnalysisType());
        assertNotNull(result.getResult());

        // 验证结果是有效的 JSON
        assertDoesNotThrow(() -> objectMapper.readTree(result.getResult()));
    }

    @Test
    @DisplayName("生成验收标准应返回多条建议")
    void analyze_acceptanceCriteria_shouldReturnMultipleCriteria() {
        // given
        when(analysisResultRepository.save(any(AiAnalysisResult.class))).thenAnswer(i -> {
            AiAnalysisResult r = i.getArgument(0);
            r.setId(1L);
            return r;
        });

        // when
        AiAnalysisResult result = mockAiAnalysisService.analyze(workItem, AnalysisType.ACCEPTANCE_CRITERIA);

        // then
        assertNotNull(result);
        assertTrue(result.getResult().contains("ACCEPTANCE_CRITERIA"));
        assertTrue(result.getResult().contains("acceptanceCriteria"));
    }

    @Test
    @DisplayName("识别风险应返回风险列表")
    void analyze_risk_shouldReturnRiskList() {
        // given
        when(analysisResultRepository.save(any(AiAnalysisResult.class))).thenAnswer(i -> {
            AiAnalysisResult r = i.getArgument(0);
            r.setId(1L);
            return r;
        });

        // when
        AiAnalysisResult result = mockAiAnalysisService.analyze(workItem, AnalysisType.RISK);

        // then
        assertNotNull(result);
        assertTrue(result.getResult().contains("RISK"));
        assertTrue(result.getResult().contains("risks"));
    }

    @Test
    @DisplayName("生成澄清问题应返回问题列表")
    void analyze_questions_shouldReturnQuestionList() {
        // given
        when(analysisResultRepository.save(any(AiAnalysisResult.class))).thenAnswer(i -> {
            AiAnalysisResult r = i.getArgument(0);
            r.setId(1L);
            return r;
        });

        // when
        AiAnalysisResult result = mockAiAnalysisService.analyze(workItem, AnalysisType.QUESTIONS);

        // then
        assertNotNull(result);
        assertTrue(result.getResult().contains("QUESTIONS"));
        assertTrue(result.getResult().contains("questions"));
    }

    @Test
    @DisplayName("生成任务拆解应返回任务列表")
    void analyze_taskBreakdown_shouldReturnTaskList() {
        // given
        when(analysisResultRepository.save(any(AiAnalysisResult.class))).thenAnswer(i -> {
            AiAnalysisResult r = i.getArgument(0);
            r.setId(1L);
            return r;
        });

        // when
        AiAnalysisResult result = mockAiAnalysisService.analyze(workItem, AnalysisType.TASK_BREAKDOWN);

        // then
        assertNotNull(result);
        assertTrue(result.getResult().contains("TASK_BREAKDOWN"));
        assertTrue(result.getResult().contains("tasks"));
    }

    @Test
    @DisplayName("获取分析历史应返回正确数据")
    void getAnalysisHistory_shouldReturnCorrectData() {
        // given
        List<AiAnalysisResult> expectedResults = List.of(
                createResult(1L, AnalysisType.SUMMARY),
                createResult(2L, AnalysisType.RISK)
        );
        when(analysisResultRepository.findByWorkItemId(1L)).thenReturn(expectedResults);

        // when
        List<AiAnalysisResult> results = mockAiAnalysisService.getAnalysisHistory(1L);

        // then
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("描述为空时生成摘要应返回提示信息")
    void analyze_summaryWithEmptyDescription_shouldReturnHint() {
        // given
        workItem.setDescription(null);
        when(analysisResultRepository.save(any(AiAnalysisResult.class))).thenAnswer(i -> {
            AiAnalysisResult r = i.getArgument(0);
            r.setId(1L);
            return r;
        });

        // when
        AiAnalysisResult result = mockAiAnalysisService.analyze(workItem, AnalysisType.SUMMARY);

        // then
        assertNotNull(result);
        assertTrue(result.getResult().contains("需求描述为空"));
    }

    private AiAnalysisResult createResult(Long id, AnalysisType type) {
        AiAnalysisResult result = new AiAnalysisResult();
        result.setId(id);
        result.setAnalysisType(type);
        result.setResult("{}");
        return result;
    }
}
