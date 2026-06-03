package com.assessment.workitem.dto;

import com.assessment.workitem.model.enums.Priority;
import com.assessment.workitem.model.enums.RiskLevel;
import com.assessment.workitem.model.enums.WorkItemType;

import java.util.List;

/**
 * 更新工作项请求
 */
public class WorkItemUpdateRequest {

    private String title;

    private String description;

    private WorkItemType type;

    private Priority priority;

    private String assignee;

    private List<String> tags;

    private String acceptanceCriteria;

    private RiskLevel riskLevel;

    public WorkItemUpdateRequest() {
    }

    public WorkItemUpdateRequest(String title, String description, WorkItemType type, Priority priority,
                                  String assignee, List<String> tags, String acceptanceCriteria,
                                  RiskLevel riskLevel) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.assignee = assignee;
        this.tags = tags;
        this.acceptanceCriteria = acceptanceCriteria;
        this.riskLevel = riskLevel;
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

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String description;
        private WorkItemType type;
        private Priority priority;
        private String assignee;
        private List<String> tags;
        private String acceptanceCriteria;
        private RiskLevel riskLevel;

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

        public Builder assignee(String assignee) {
            this.assignee = assignee;
            return this;
        }

        public Builder tags(List<String> tags) {
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

        public WorkItemUpdateRequest build() {
            return new WorkItemUpdateRequest(title, description, type, priority, assignee, tags,
                    acceptanceCriteria, riskLevel);
        }
    }
}
