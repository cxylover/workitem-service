package com.assessment.workitem.dto;

import com.assessment.workitem.model.enums.QuestionStatus;

/**
 * 更新澄清新问题请求
 */
public class ClarificationUpdateRequest {

    private QuestionStatus status;

    private String answer;

    public ClarificationUpdateRequest() {
    }

    public ClarificationUpdateRequest(QuestionStatus status, String answer) {
        this.status = status;
        this.answer = answer;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private QuestionStatus status;
        private String answer;

        public Builder status(QuestionStatus status) {
            this.status = status;
            return this;
        }

        public Builder answer(String answer) {
            this.answer = answer;
            return this;
        }

        public ClarificationUpdateRequest build() {
            return new ClarificationUpdateRequest(status, answer);
        }
    }
}
