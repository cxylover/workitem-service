package com.assessment.workitem.dto;

import com.assessment.workitem.model.enums.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 创建澄清问题请求
 */
public class ClarificationCreateRequest {

    @NotBlank(message = "问题内容不能为空")
    private String content;

    @NotNull(message = "严重程度不能为空")
    private Severity severity;

    public ClarificationCreateRequest() {
    }

    public ClarificationCreateRequest(String content, Severity severity) {
        this.content = content;
        this.severity = severity;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String content;
        private Severity severity;

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder severity(Severity severity) {
            this.severity = severity;
            return this;
        }

        public ClarificationCreateRequest build() {
            return new ClarificationCreateRequest(content, severity);
        }
    }
}
