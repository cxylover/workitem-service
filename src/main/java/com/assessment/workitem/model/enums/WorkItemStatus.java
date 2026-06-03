package com.assessment.workitem.model.enums;

/**
 * 工作项状态枚举
 * 定义工作项的生命周期状态
 */
public enum WorkItemStatus {

    /** 草稿 */
    DRAFT,

    /** 分析中 */
    ANALYZING,

    /** 已准备 */
    READY,

    /** 开发中 */
    IN_PROGRESS,

    /** 测试中 */
    TESTING,

    /** 已完成 */
    COMPLETED
}
