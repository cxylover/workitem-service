package com.assessment.workitem.service;

import com.assessment.workitem.dto.WorkItemCreateRequest;
import com.assessment.workitem.dto.WorkItemUpdateRequest;
import com.assessment.workitem.exception.ResourceNotFoundException;
import com.assessment.workitem.model.entity.WorkItem;
import com.assessment.workitem.model.enums.Priority;
import com.assessment.workitem.model.enums.WorkItemStatus;
import com.assessment.workitem.model.enums.WorkItemType;
import com.assessment.workitem.repository.WorkItemRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 工作项服务
 */
@Service
public class WorkItemService implements IWorkItemService {

    private static final Logger log = LoggerFactory.getLogger(WorkItemService.class);

    private final WorkItemRepository workItemRepository;
    private final ObjectMapper objectMapper;

    public WorkItemService(WorkItemRepository workItemRepository, ObjectMapper objectMapper) {
        this.workItemRepository = workItemRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 创建工作项
     */
    @Transactional
    public WorkItem create(WorkItemCreateRequest request) {
        WorkItem workItem = WorkItem.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .priority(request.getPriority())
                .status(WorkItemStatus.DRAFT)
                .assignee(request.getAssignee())
                .tags(convertTagsToJson(request.getTags()))
                .acceptanceCriteria(request.getAcceptanceCriteria())
                .riskLevel(request.getRiskLevel())
                .build();

        WorkItem saved = workItemRepository.save(workItem);
        log.info("创建工作项: {}", saved.getId());
        return saved;
    }

    /**
     * 更新工作项
     */
    @Transactional
    public WorkItem update(Long id, WorkItemUpdateRequest request) {
        WorkItem workItem = getById(id);

        if (request.getTitle() != null) {
            workItem.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            workItem.setDescription(request.getDescription());
        }
        if (request.getType() != null) {
            workItem.setType(request.getType());
        }
        if (request.getPriority() != null) {
            workItem.setPriority(request.getPriority());
        }
        if (request.getAssignee() != null) {
            workItem.setAssignee(request.getAssignee());
        }
        if (request.getTags() != null) {
            workItem.setTags(convertTagsToJson(request.getTags()));
        }
        if (request.getAcceptanceCriteria() != null) {
            workItem.setAcceptanceCriteria(request.getAcceptanceCriteria());
        }
        if (request.getRiskLevel() != null) {
            workItem.setRiskLevel(request.getRiskLevel());
        }

        WorkItem saved = workItemRepository.save(workItem);
        log.info("更新工作项: {}", saved.getId());
        return saved;
    }

    /**
     * 获取工作项详情
     */
    public WorkItem getById(Long id) {
        return workItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("工作项", id));
    }

    /**
     * 获取工作项列表（支持筛选）
     */
    public List<WorkItem> list(WorkItemStatus status, WorkItemType type, Priority priority, String assignee) {
        if (status != null && type != null) {
            return workItemRepository.findByStatusAndType(status, type);
        }
        if (status != null) {
            return workItemRepository.findByStatus(status);
        }
        if (type != null) {
            return workItemRepository.findByType(type);
        }
        if (priority != null) {
            return workItemRepository.findByPriority(priority);
        }
        if (assignee != null) {
            return workItemRepository.findByAssignee(assignee);
        }
        return workItemRepository.findAll();
    }

    /**
     * 删除工作项
     */
    @Transactional
    public void delete(Long id) {
        WorkItem workItem = getById(id);
        workItemRepository.delete(workItem);
        log.info("删除工作项: {}", id);
    }

    /**
     * 将标签列表转换为 JSON 字符串
     */
    private String convertTagsToJson(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(tags);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
