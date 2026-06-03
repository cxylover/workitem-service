package com.assessment.workitem.dto;

import com.assessment.workitem.model.enums.WorkItemStatus;
import jakarta.validation.constraints.NotNull;

/**
 * 状态流转请求
 */
public class TransitionRequest {

    @NotNull(message = "目标状态不能为空")
    private WorkItemStatus targetStatus;

    private String reason;

    private String changedBy;

    public TransitionRequest() {
    }

    public TransitionRequest(WorkItemStatus targetStatus, String reason, String changedBy) {
        this.targetStatus = targetStatus;
        this.reason = reason;
        this.changedBy = changedBy;
    }

    public WorkItemStatus getTargetStatus() {
        return targetStatus;
    }

    public void setTargetStatus(WorkItemStatus targetStatus) {
        this.targetStatus = targetStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private WorkItemStatus targetStatus;
        private String reason;
        private String changedBy;

        public Builder targetStatus(WorkItemStatus targetStatus) {
            this.targetStatus = targetStatus;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Builder changedBy(String changedBy) {
            this.changedBy = changedBy;
            return this;
        }

        public TransitionRequest build() {
            return new TransitionRequest(targetStatus, reason, changedBy);
        }
    }
}
