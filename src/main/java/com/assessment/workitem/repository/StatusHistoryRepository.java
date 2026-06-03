package com.assessment.workitem.repository;

import com.assessment.workitem.model.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 状态历史 Repository
 */
@Repository
public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {

    /** 按工作项查询，按时间倒序 */
    List<StatusHistory> findByWorkItemIdOrderByChangedAtDesc(Long workItemId);
}
