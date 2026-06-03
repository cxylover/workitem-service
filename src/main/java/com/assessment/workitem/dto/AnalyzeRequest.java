package com.assessment.workitem.dto;

import com.assessment.workitem.model.enums.AnalysisType;
import jakarta.validation.constraints.NotNull;

/**
 * AI 分析请求
 */
public class AnalyzeRequest {

    @NotNull(message = "分析类型不能为空")
    private AnalysisType type;

    public AnalyzeRequest() {
    }

    public AnalyzeRequest(AnalysisType type) {
        this.type = type;
    }

    public AnalysisType getType() {
        return type;
    }

    public void setType(AnalysisType type) {
        this.type = type;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private AnalysisType type;

        public Builder type(AnalysisType type) {
            this.type = type;
            return this;
        }

        public AnalyzeRequest build() {
            return new AnalyzeRequest(type);
        }
    }
}
