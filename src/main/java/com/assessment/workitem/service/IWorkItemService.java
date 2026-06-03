package com.assessment.workitem.service;

import com.assessment.workitem.dto.WorkItemCreateRequest;
import com.assessment.workitem.dto.WorkItemUpdateRequest;
import com.assessment.workitem.model.entity.WorkItem;
import com.assessment.workitem.model.enums.Priority;
import com.assessment.workitem.model.enums.WorkItemStatus;
import com.assessment.workitem.model.enums.WorkItemType;

import java.util.List;

/**
 * 工作项服务接口
 */
public interface IWorkItemService {

    WorkItem create(WorkItemCreateRequest request);

    WorkItem update(Long id, WorkItemUpdateRequest request);

    WorkItem getById(Long id);

    List<WorkItem> list(WorkItemStatus status, WorkItemType type, Priority priority, String assignee);

    void delete(Long id);
}
