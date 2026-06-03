package com.assessment.workitem.model.entity;

import com.assessment.workitem.model.enums.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 工作项实体
 */
@Entity
@Table(name = "work_items")
public class WorkItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 标题 */
    @Column(nullable = false)
    private String title;

    /** 描述 */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** 类型 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkItemType type;

    /** 优先级 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    /** 当前状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkItemStatus status;

    /** 负责人 */
    private String assignee;

    /** 标签（JSON 数组格式存储） */
    @Column(columnDefinition = "TEXT")
    private String tags;

    /** 验收标准 */
    @Column(columnDefinition = "TEXT")
    private String acceptanceCriteria;

    /** 风险等级 */
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    /** 创建时间 */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 澄清问题列表 */
    @OneToMany(mappedBy = "workItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClarificationQuestion> clarifications = new ArrayList<>();

    /** 状态历史 */
    @OneToMany(mappedBy = "workItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("changedAt DESC")
    private List<StatusHistory> statusHistory = new ArrayList<>();

    /** AI 分析结果 */
    @OneToMany(mappedBy = "workItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<AiAnalysisResult> analysisResults = new ArrayList<>();

    public WorkItem() {
    }

    public WorkItem(Long id, String title, String description, WorkItemType type, Priority priority,
                    WorkItemStatus status, String assignee, String tags, String acceptanceCriteria,
                    RiskLevel riskLevel, LocalDateTime createdAt, LocalDateTime updatedAt,
                    List<ClarificationQuestion> clarifications, List<StatusHistory> statusHistory,
                    List<AiAnalysisResult> analysisResults) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.status = status;
        this.assignee = assignee;
        this.tags = tags;
        this.acceptanceCriteria = acceptanceCriteria;
        this.riskLevel = riskLevel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.clarifications = clarifications != null ? clarifications : new ArrayList<>();
        this.statusHistory = statusHistory != null ? statusHistory : new ArrayList<>();
        this.analysisResults = analysisResults != null ? analysisResults : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WorkItemType getType() {
        return type;
    }

    public void setType(WorkItemType type) {
        this.type = type;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public WorkItemStatus getStatus() {
        return status;
    }

    public void setStatus(WorkItemStatus status) {
        this.status = status;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public void setAcceptanceCriteria(String acceptanceCriteria) {
        this.acceptanceCriteria = acceptanceCriteria;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ClarificationQuestion> getClarifications() {
        return clarifications;
    }

    public void setClarifications(List<ClarificationQuestion> clarifications) {
        this.clarifications = clarifications;
    }

    public List<StatusHistory> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<StatusHistory> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public List<AiAnalysisResult> getAnalysisResults() {
        return analysisResults;
    }

    public void setAnalysisResults(List<AiAnalysisResult> analysisResults) {
        this.analysisResults = analysisResults;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String title;
        private String description;
        private WorkItemType type;
        private Priority priority;
        private WorkItemStatus status;
        private String assignee;
        private String tags;
        private String acceptanceCriteria;
        private RiskLevel riskLevel;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<ClarificationQuestion> clarifications = new ArrayList<>();
        private List<StatusHistory> statusHistory = new ArrayList<>();
        private List<AiAnalysisResult> analysisResults = new ArrayList<>();

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder type(WorkItemType type) {
            this.type = type;
            return this;
        }

        public Builder priority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public Builder status(WorkItemStatus status) {
            this.status = status;
            return this;
        }

        public Builder assignee(String assignee) {
            this.assignee = assignee;
            return this;
        }

        public Builder tags(String tags) {
            this.tags = tags;
            return this;
        }

        public Builder acceptanceCriteria(String acceptanceCriteria) {
            this.acceptanceCriteria = acceptanceCriteria;
            return this;
        }

        public Builder riskLevel(RiskLevel riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder clarifications(List<ClarificationQuestion> clarifications) {
            this.clarifications = clarifications;
            return this;
        }

        public Builder statusHistory(List<StatusHistory> statusHistory) {
            this.statusHistory = statusHistory;
            return this;
        }

        public Builder analysisResults(List<AiAnalysisResult> analysisResults) {
            this.analysisResults = analysisResults;
            return this;
        }

        public WorkItem build() {
            return new WorkItem(id, title, description, type, priority, status, assignee, tags,
                    acceptanceCriteria, riskLevel, createdAt, updatedAt, clarifications, statusHistory,
                    analysisResults);
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = WorkItemStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
