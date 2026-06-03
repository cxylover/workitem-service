package com.assessment.workitem.controller;

import com.assessment.workitem.dto.*;
import com.assessment.workitem.model.entity.StatusHistory;
import com.assessment.workitem.model.entity.WorkItem;
import com.assessment.workitem.model.enums.Priority;
import com.assessment.workitem.model.enums.WorkItemStatus;
import com.assessment.workitem.model.enums.WorkItemType;
import com.assessment.workitem.service.StatusTransitionService;
import com.assessment.workitem.service.WorkItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 工作项控制器
 */
@RestController
@RequestMapping("/api/work-items")
public class WorkItemController {

    private final WorkItemService workItemService;
    private final StatusTransitionService statusTransitionService;

    public WorkItemController(WorkItemService workItemService,
                              StatusTransitionService statusTransitionService) {
        this.workItemService = workItemService;
        this.statusTransitionService = statusTransitionService;
    }

    /**
     * 创建工作项
     */
    @PostMapping
    public ResponseEntity<ApiResponse<WorkItem>> create(@Valid @RequestBody WorkItemCreateRequest request) {
        WorkItem workItem = workItemService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("创建工作项成功", workItem));
    }

    /**
     * 获取工作项列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<WorkItem>>> list(
            @RequestParam(required = false) WorkItemStatus status,
            @RequestParam(required = false) WorkItemType type,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String assignee) {
        List<WorkItem> workItems = workItemService.list(status, type, priority, assignee);
        return ResponseEntity.ok(ApiResponse.success(workItems));
    }

    /**
     * 获取工作项详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkItem>> getById(@PathVariable Long id) {
        WorkItem workItem = workItemService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(workItem));
    }

    /**
     * 更新工作项
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorkItem>> update(
            @PathVariable Long id,
            @Valid @RequestBody WorkItemUpdateRequest request) {
        WorkItem workItem = workItemService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("更新工作项成功", workItem));
    }

    /**
     * 删除工作项
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        workItemService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("删除工作项成功", null));
    }

    /**
     * 执行状态流转
     */
    @PostMapping("/{id}/transition")
    public ResponseEntity<ApiResponse<StatusHistory>> transition(
            @PathVariable Long id,
            @Valid @RequestBody TransitionRequest request) {
        WorkItem workItem = workItemService.getById(id);
        StatusHistory history = statusTransitionService.transition(
                workItem,
                request.getTargetStatus(),
                request.getReason(),
                request.getChangedBy());
        return ResponseEntity.ok(ApiResponse.success("状态流转成功", history));
    }

    /**
     * 获取状态流转历史
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<StatusHistory>>> getHistory(@PathVariable Long id) {
        List<StatusHistory> history = statusTransitionService.getHistory(id);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * 获取当前状态可流转的目标状态
     */
    @GetMapping("/{id}/allowed-transitions")
    public ResponseEntity<ApiResponse<Set<WorkItemStatus>>> getAllowedTransitions(@PathVariable Long id) {
        WorkItem workItem = workItemService.getById(id);
        Set<WorkItemStatus> allowed = statusTransitionService.getAllowedTransitions(workItem.getStatus());
        return ResponseEntity.ok(ApiResponse.success(allowed));
    }
}
