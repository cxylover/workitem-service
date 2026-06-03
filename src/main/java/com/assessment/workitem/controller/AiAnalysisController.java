package com.assessment.workitem.controller;

import com.assessment.workitem.dto.AnalyzeRequest;
import com.assessment.workitem.dto.ApiResponse;
import com.assessment.workitem.model.entity.AiAnalysisResult;
import com.assessment.workitem.model.entity.WorkItem;
import com.assessment.workitem.service.AiAnalysisService;
import com.assessment.workitem.service.WorkItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI 分析控制器
 */
@RestController
@RequestMapping("/api/work-items/{workItemId}/analyze")
public class AiAnalysisController {

    private final AiAnalysisService aiAnalysisService;
    private final WorkItemService workItemService;

    public AiAnalysisController(AiAnalysisService aiAnalysisService,
                                WorkItemService workItemService) {
        this.aiAnalysisService = aiAnalysisService;
        this.workItemService = workItemService;
    }

    /**
     * 触发 AI 分析
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AiAnalysisResult>> analyze(
            @PathVariable Long workItemId,
            @Valid @RequestBody AnalyzeRequest request) {
        WorkItem workItem = workItemService.getById(workItemId);
        AiAnalysisResult result = aiAnalysisService.analyze(workItem, request.getType());
        return ResponseEntity.ok(ApiResponse.success("AI 分析完成", result));
    }

    /**
     * 获取分析历史
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AiAnalysisResult>>> getHistory(@PathVariable Long workItemId) {
        List<AiAnalysisResult> results = aiAnalysisService.getAnalysisHistory(workItemId);
        return ResponseEntity.ok(ApiResponse.success(results));
    }
}
