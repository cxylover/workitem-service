/**
 * API 请求模块
 */
const API = {
    baseUrl: '/api',

    /**
     * 发送请求
     */
    async request(url, options = {}) {
        const response = await fetch(this.baseUrl + url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || '请求失败');
        }

        return data;
    },

    /**
     * 工作项相关 API
     */
    workItems: {
        /**
         * 获取工作项列表
         */
        async list(params = {}) {
            const query = new URLSearchParams();
            if (params.status) query.append('status', params.status);
            if (params.type) query.append('type', params.type);
            if (params.priority) query.append('priority', params.priority);
            if (params.assignee) query.append('assignee', params.assignee);
            const queryStr = query.toString();
            return API.request('/work-items' + (queryStr ? '?' + queryStr : ''));
        },

        /**
         * 获取工作项详情
         */
        async getById(id) {
            return API.request(`/work-items/${id}`);
        },

        /**
         * 创建工作项
         */
        async create(data) {
            return API.request('/work-items', {
                method: 'POST',
                body: JSON.stringify(data)
            });
        },

        /**
         * 更新工作项
         */
        async update(id, data) {
            return API.request(`/work-items/${id}`, {
                method: 'PUT',
                body: JSON.stringify(data)
            });
        },

        /**
         * 删除工作项
         */
        async delete(id) {
            return API.request(`/work-items/${id}`, {
                method: 'DELETE'
            });
        },

        /**
         * 状态流转
         */
        async transition(id, data) {
            return API.request(`/work-items/${id}/transition`, {
                method: 'POST',
                body: JSON.stringify(data)
            });
        },

        /**
         * 获取状态历史
         */
        async getHistory(id) {
            return API.request(`/work-items/${id}/history`);
        },

        /**
         * 获取允许的状态流转
         */
        async getAllowedTransitions(id) {
            return API.request(`/work-items/${id}/allowed-transitions`);
        }
    },

    /**
     * 澄清问题相关 API
     */
    clarifications: {
        /**
         * 获取澄清问题列表
         */
        async list(workItemId, unresolved = false) {
            const query = unresolved ? '?unresolved=true' : '';
            return API.request(`/work-items/${workItemId}/clarifications${query}`);
        },

        /**
         * 创建澄清问题
         */
        async create(workItemId, data) {
            return API.request(`/work-items/${workItemId}/clarifications`, {
                method: 'POST',
                body: JSON.stringify(data)
            });
        },

        /**
         * 更新澄清问题
         */
        async update(workItemId, id, data) {
            return API.request(`/work-items/${workItemId}/clarifications/${id}`, {
                method: 'PUT',
                body: JSON.stringify(data)
            });
        }
    },

    /**
     * AI 分析相关 API
     */
    analysis: {
        /**
         * 触发 AI 分析
         */
        async analyze(workItemId, type) {
            return API.request(`/work-items/${workItemId}/analyze`, {
                method: 'POST',
                body: JSON.stringify({ type })
            });
        },

        /**
         * 获取分析历史
         */
        async getHistory(workItemId) {
            return API.request(`/work-items/${workItemId}/analyze`);
        }
    }
};

/**
 * 工具函数
 */
const Utils = {
    /**
     * 显示消息提示
     */
    showToast(message, type = 'success') {
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.textContent = message;
        document.body.appendChild(toast);

        setTimeout(() => {
            toast.remove();
        }, 3000);
    },

    /**
     * 获取状态显示文本
     */
    getStatusText(status) {
        const statusMap = {
            'DRAFT': '草稿',
            'ANALYZING': '分析中',
            'READY': '已准备',
            'IN_PROGRESS': '开发中',
            'TESTING': '测试中',
            'COMPLETED': '已完成'
        };
        return statusMap[status] || status;
    },

    /**
     * 获取状态 CSS 类
     */
    getStatusClass(status) {
        return `badge-${status.toLowerCase().replace('_', '-')}`;
    },

    /**
     * 获取优先级显示文本
     */
    getPriorityText(priority) {
        return priority;
    },

    /**
     * 获取优先级 CSS 类
     */
    getPriorityClass(priority) {
        return `priority-${priority.toLowerCase()}`;
    },

    /**
     * 获取类型显示文本
     */
    getTypeText(type) {
        const typeMap = {
            'STORY': '需求',
            'BUG': '缺陷',
            'TASK': '任务'
        };
        return typeMap[type] || type;
    },

    /**
     * 获取严重程度 CSS 类
     */
    getSeverityClass(severity) {
        return `severity-${severity.toLowerCase()}`;
    },

    /**
     * 获取严重程度显示文本
     */
    getSeverityText(severity) {
        const severityMap = {
            'HIGH': '高',
            'MEDIUM': '中',
            'LOW': '低'
        };
        return severityMap[severity] || severity;
    },

    /**
     * 格式化日期
     */
    formatDate(dateStr) {
        if (!dateStr) return '-';
        const date = new Date(dateStr);
        return date.toLocaleString('zh-CN');
    },

    /**
     * 解析 JSON 标签
     */
    parseTags(tagsStr) {
        try {
            return JSON.parse(tagsStr);
        } catch {
            return [];
        }
    }
};
