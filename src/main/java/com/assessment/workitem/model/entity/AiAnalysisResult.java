package com.assessment.workitem.model.entity;

import com.assessment.workitem.model.enums.AnalysisType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * AI 分析结果实体
 */
@Entity
@Table(name = "ai_analysis_results")
public class AiAnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属工作项 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_item_id", nullable = false)
    @JsonIgnore
    private WorkItem workItem;

    /** 分析类型 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisType analysisType;

    /** 分析结果（JSON 格式） */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String result;

    /** 创建时间 */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public AiAnalysisResult() {
    }

    public AiAnalysisResult(Long id, WorkItem workItem, AnalysisType analysisType, String result,
                            LocalDateTime createdAt) {
        this.id = id;
        this.workItem = workItem;
        this.analysisType = analysisType;
        this.result = result;
        this.createdAt = createdAt;
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

    public AnalysisType getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(AnalysisType analysisType) {
        this.analysisType = analysisType;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private WorkItem workItem;
        private AnalysisType analysisType;
        private String result;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder workItem(WorkItem workItem) {
            this.workItem = workItem;
            return this;
        }

        public Builder analysisType(AnalysisType analysisType) {
            this.analysisType = analysisType;
            return this;
        }

        public Builder result(String result) {
            this.result = result;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AiAnalysisResult build() {
            return new AiAnalysisResult(id, workItem, analysisType, result, createdAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
