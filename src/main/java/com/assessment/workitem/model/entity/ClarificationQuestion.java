package com.assessment.workitem.model.entity;

import com.assessment.workitem.model.enums.QuestionStatus;
import com.assessment.workitem.model.enums.Severity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 澄清问题实体
 */
@Entity
@Table(name = "clarification_questions")
public class ClarificationQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属工作项 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_item_id", nullable = false)
    @JsonIgnore
    private WorkItem workItem;

    /** 问题内容 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 严重程度 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    /** 状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus status;

    /** 回答 */
    @Column(columnDefinition = "TEXT")
    private String answer;

    /** 创建时间 */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** 解决时间 */
    private LocalDateTime resolvedAt;

    public ClarificationQuestion() {
    }

    public ClarificationQuestion(Long id, WorkItem workItem, String content, Severity severity,
                                 QuestionStatus status, String answer, LocalDateTime createdAt,
                                 LocalDateTime resolvedAt) {
        this.id = id;
        this.workItem = workItem;
        this.content = content;
        this.severity = severity;
        this.status = status;
        this.answer = answer;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public QuestionStatus getStatus() {
        return status;
    }

    public void setStatus(QuestionStatus status) {
        this.status = status;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private WorkItem workItem;
        private String content;
        private Severity severity;
        private QuestionStatus status;
        private String answer;
        private LocalDateTime createdAt;
        private LocalDateTime resolvedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder workItem(WorkItem workItem) {
            this.workItem = workItem;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder severity(Severity severity) {
            this.severity = severity;
            return this;
        }

        public Builder status(QuestionStatus status) {
            this.status = status;
            return this;
        }

        public Builder answer(String answer) {
            this.answer = answer;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder resolvedAt(LocalDateTime resolvedAt) {
            this.resolvedAt = resolvedAt;
            return this;
        }

        public ClarificationQuestion build() {
            return new ClarificationQuestion(id, workItem, content, severity, status, answer, createdAt, resolvedAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = QuestionStatus.UNRESOLVED;
        }
    }
}
