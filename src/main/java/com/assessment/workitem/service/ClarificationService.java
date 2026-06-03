package com.assessment.workitem.service;

import com.assessment.workitem.dto.ClarificationCreateRequest;
import com.assessment.workitem.dto.ClarificationUpdateRequest;
import com.assessment.workitem.exception.BusinessException;
import com.assessment.workitem.exception.ResourceNotFoundException;
import com.assessment.workitem.model.entity.ClarificationQuestion;
import com.assessment.workitem.model.entity.WorkItem;
import com.assessment.workitem.model.enums.QuestionStatus;
import com.assessment.workitem.repository.ClarificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 澄清问题服务
 */
@Service
public class ClarificationService {

    private static final Logger log = LoggerFactory.getLogger(ClarificationService.class);

    private final ClarificationRepository clarificationRepository;
    private final IWorkItemService workItemService;

    public ClarificationService(ClarificationRepository clarificationRepository,
                                IWorkItemService workItemService) {
        this.clarificationRepository = clarificationRepository;
        this.workItemService = workItemService;
    }

    /**
     * 新增澄清问题
     */
    @Transactional
    public ClarificationQuestion create(Long workItemId, ClarificationCreateRequest request) {
        WorkItem workItem = workItemService.getById(workItemId);

        ClarificationQuestion question = ClarificationQuestion.builder()
                .workItem(workItem)
                .content(request.getContent())
                .severity(request.getSeverity())
                .status(QuestionStatus.UNRESOLVED)
                .build();

        ClarificationQuestion saved = clarificationRepository.save(question);
        log.info("为工作项 [{}] 创建澄清问题: {}", workItemId, saved.getId());
        return saved;
    }

    /**
     * 更新澄清问题（解决）
     */
    @Transactional
    public ClarificationQuestion update(Long id, ClarificationUpdateRequest request) {
        ClarificationQuestion question = getById(id);

        if (request.getStatus() != null) {
            // 如果标记为已解决，记录解决时间
            if (request.getStatus() == QuestionStatus.RESOLVED && question.getStatus() == QuestionStatus.UNRESOLVED) {
                question.setResolvedAt(LocalDateTime.now());
            }
            question.setStatus(request.getStatus());
        }

        if (request.getAnswer() != null) {
            question.setAnswer(request.getAnswer());
        }

        ClarificationQuestion saved = clarificationRepository.save(question);
        log.info("更新澄清问题: {}", saved.getId());
        return saved;
    }

    /**
     * 获取澄清问题详情
     */
    public ClarificationQuestion getById(Long id) {
        return clarificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("澄清问题", id));
    }

    /**
     * 获取工作项下的澄清问题列表
     */
    public List<ClarificationQuestion> listByWorkItem(Long workItemId) {
        return clarificationRepository.findByWorkItemId(workItemId);
    }

    /**
     * 获取工作项下未解决的澄清问题
     */
    public List<ClarificationQuestion> listUnresolved(Long workItemId) {
        return clarificationRepository.findByWorkItemIdAndStatus(workItemId, QuestionStatus.UNRESOLVED);
    }

    /**
     * 获取工作项下高优先级未解决的澄清问题
     */
    public List<ClarificationQuestion> listHighSeverityUnresolved(Long workItemId) {
        return clarificationRepository.findByWorkItemIdAndSeverityAndStatus(
                workItemId, com.assessment.workitem.model.enums.Severity.HIGH, QuestionStatus.UNRESOLVED);
    }
}
