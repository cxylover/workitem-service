package com.assessment.workitem.model.entity;

import com.assessment.workitem.model.enums.WorkItemStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 状态历史实体
 */
@Entity
@Table(name = "status_history")
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属工作项 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_item_id", nullable = false)
    @JsonIgnore
    private WorkItem workItem;

    /** 原状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkItemStatus fromStatus;

    /** 目标状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkItemStatus toStatus;

    /** 操作人 */
    private String changedBy;

    /** 变更时间 */
    @Column(updatable = false)
    private LocalDateTime changedAt;

    /** 变更原因 */
    @Column(columnDefinition = "TEXT")
    private String reason;

    public StatusHistory() {
    }

    public StatusHistory(Long id, WorkItem workItem, WorkItemStatus fromStatus, WorkItemStatus toStatus,
                         String changedBy, LocalDateTime changedAt, String reason) {
        this.id = id;
        this.workItem = workItem;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkItem getWorkItem() {
        return workItem;
    }

    public void setWorkItem(WorkItem workItem) {
        this.workItem = workItem;
    }

    public WorkItemStatus getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(WorkItemStatus fromStatus) {
        this.fromStatus = fromStatus;
    }

    public WorkItemStatus getToStatus() {
        return toStatus;
    }

    public void setToStatus(WorkItemStatus toStatus) {
        this.toStatus = toStatus;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private WorkItem workItem;
        private WorkItemStatus fromStatus;
        private WorkItemStatus toStatus;
        private String changedBy;
        private LocalDateTime changedAt;
        private String reason;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder workItem(WorkItem workItem) {
            this.workItem = workItem;
            return this;
        }

        public Builder fromStatus(WorkItemStatus fromStatus) {
            this.fromStatus = fromStatus;
            return this;
        }

        public Builder toStatus(WorkItemStatus toStatus) {
            this.toStatus = toStatus;
            return this;
        }

        public Builder changedBy(String changedBy) {
            this.changedBy = changedBy;
            return this;
        }

        public Builder changedAt(LocalDateTime changedAt) {
            this.changedAt = changedAt;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public StatusHistory build() {
            return new StatusHistory(id, workItem, fromStatus, toStatus, changedBy, changedAt, reason);
        }
    }

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }
}
