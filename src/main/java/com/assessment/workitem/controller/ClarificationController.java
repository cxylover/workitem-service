package com.assessment.workitem.controller;

import com.assessment.workitem.dto.ApiResponse;
import com.assessment.workitem.dto.ClarificationCreateRequest;
import com.assessment.workitem.dto.ClarificationUpdateRequest;
import com.assessment.workitem.model.entity.ClarificationQuestion;
import com.assessment.workitem.service.ClarificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 澄清问题控制器
 */
@RestController
@RequestMapping("/api/work-items/{workItemId}/clarifications")
public class ClarificationController {

    private final ClarificationService clarificationService;

    public ClarificationController(ClarificationService clarificationService) {
        this.clarificationService = clarificationService;
    }

    /**
     * 新增澄清问题
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ClarificationQuestion>> create(
            @PathVariable Long workItemId,
            @Valid @RequestBody ClarificationCreateRequest request) {
        ClarificationQuestion question = clarificationService.create(workItemId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("创建澄清问题成功", question));
    }

    /**
     * 获取工作项下的澄清问题列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ClarificationQuestion>>> list(
            @PathVariable Long workItemId,
            @RequestParam(required = false) Boolean unresolved) {
        List<ClarificationQuestion> questions;
        if (Boolean.TRUE.equals(unresolved)) {
            questions = clarificationService.listUnresolved(workItemId);
        } else {
            questions = clarificationService.listByWorkItem(workItemId);
        }
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    /**
     * 获取澄清问题详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClarificationQuestion>> getById(@PathVariable Long id) {
        ClarificationQuestion question = clarificationService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(question));
    }

    /**
     * 更新澄清问题（解决）
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClarificationQuestion>> update(
            @PathVariable Long id,
            @Valid @RequestBody ClarificationUpdateRequest request) {
        ClarificationQuestion question = clarificationService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("更新澄清问题成功", question));
    }
}
