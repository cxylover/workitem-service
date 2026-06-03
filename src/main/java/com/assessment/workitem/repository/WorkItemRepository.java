package com.assessment.workitem.repository;

import com.assessment.workitem.model.entity.WorkItem;
import com.assessment.workitem.model.enums.Priority;
import com.assessment.workitem.model.enums.WorkItemStatus;
import com.assessment.workitem.model.enums.WorkItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 工作项 Repository
 */
@Repository
public interface WorkItemRepository extends JpaRepository<WorkItem, Long> {

    /** 按状态查询 */
    List<WorkItem> findByStatus(WorkItemStatus status);

    /** 按类型查询 */
    List<WorkItem> findByType(WorkItemType type);

    /** 按优先级查询 */
    List<WorkItem> findByPriority(Priority priority);

    /** 按负责人查询 */
    List<WorkItem> findByAssignee(String assignee);

    /** 按状态和类型查询 */
    List<WorkItem> findByStatusAndType(WorkItemStatus status, WorkItemType type);
}
