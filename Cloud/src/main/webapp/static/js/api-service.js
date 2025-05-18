/**
 * API服务模块
 * 封装所有API请求
 */

// // API基础路径
// const API_BASE_URL = 'http://localhost:8081/Cloud';

// 通用请求方法
const request = async (endpoint, method = 'GET', data = null) => {
    const url = endpoint.startsWith('http') ? endpoint : API_BASE_URL + endpoint;

    const options = {
        method,
        headers: {
            'Content-Type': 'application/json'
        }
    };

    // 如果有请求体，添加到options
    if (data) {
        options.body = JSON.stringify(data);
    }

    try {
        const response = await fetch(url, options);
        const result = await response.json();

        // 统一返回格式
        return {
            code: result.code || response.status,
            data: result.data,
            message: result.message || response.statusText
        };
    } catch (error) {
        console.error(`API请求错误: ${endpoint}`, error);
        return {
            code: 500,
            data: null,
            message: error.message
        };
    }
};

// API服务对象
const ApiService = {
    // 认证相关API
    auth: {
        // 登录
        login: (username, password, captcha, captchaToken) => {
            return request('/login', 'POST', {
                username,
                password,
                captcha,
                captchaToken
            });
        },

        // 注册
        register: (username, password, email, emailCode, captcha) => {
            return request('/register', 'POST', {
                username,
                password,
                email,
                emailCode,
                captcha
            });
        },

        // 获取图形验证码
        getCaptcha: () => {
            return request('/captcha');
        },

        // 发送邮箱验证码
        sendEmailCode: (email) => {
            return request('/email-code', 'POST', { email });
        },

        // 发送重置密码验证码
        sendResetCode: (email) => {
            return request('/reset-code', 'POST', { email });
        },

        // 重置密码
        resetPassword: (email, code, password) => {
            return request('/reset-password', 'POST', {
                email: email,
                emailCode: code,
                newPassword: password
            });
        },

        // 检查用户名是否可用
        checkUsername: (username) => {
            return request(`/check-username?username=${encodeURIComponent(username)}`);
        },

        // 获取当前用户信息
        getCurrentUser: () => {
            return request('/getuserinfo');
        },

        // 退出登录
        logout: () => {
            return request('/logout', 'POST');
        }
    },

    // 知识库相关API
    knowledgeBase: {
        // 获取我的知识库列表
        getMyList: function(page = 1, size = 10) {
            return request(`/knowledge/list/my?page=${page}&size=${size}`);
        },

        // 获取公开知识库列表
        getPublicList: function(page = 1, size = 9) {
            return request(`/knowledge/list/public?page=${page}&size=${size}`);
        },

        // 获取知识库详情
        getDetail: function(id) {
            return request(`/knowledge/${id}`);
        },

        // 创建知识库
        create: function(name, description, isPublic) {
            return request('/knowledge', 'POST', {
                name: name,
                description: description,
                isPublic: isPublic
            });
        },

        // 更新知识库
        update: function(id, name, description, isPublic) {
            return request(`/knowledge/${id}`, 'PUT', {
                name: name,
                description: description,
                isPublic: isPublic
            });
        },

        // 删除知识库
        delete: function(id) {
            return request(`/knowledge/${id}`, 'DELETE');
        },

        // 点赞知识库
        toggleFavorite: (id, isFavorite) => {
            return request(`/knowledge/favorite`, 'POST', {
                knowledgeBaseId: id,
                favorite: isFavorite
            });
        }
    },

    // 知识库成员相关API
    knowledgeMember: {
        // 获取成员列表
        getList: (knowledgeBaseId) => {
            return request(`/knowledge/${knowledgeBaseId}/members`);
        },

        add: function(knowledgeBaseId, userId, role) {
            return request('/knowledge/member', 'POST', {
                knowledgeBaseId: knowledgeBaseId,
                userId: userId,
                role: role
            });
        },

        updateRole: function(knowledgeBaseId, userId, role) {
            return request('/knowledge/member', 'PUT', {
                knowledgeBaseId: knowledgeBaseId,
                userId: userId,
                role: role
            });
        },

        remove: function(knowledgeBaseId, memberId) {
            return request(`/knowledge/member/${knowledgeBaseId}/${memberId}`, 'DELETE');
        },
        // 通过用户名或邮箱添加成员
        addByIdentifier: (knowledgeBaseId, identifier, role) => {
            return request('/knowledge/member/invite', 'POST', {
                knowledgeBaseId,
                identifier,
                role
            });
        }
    },

    // 文章相关API
    article: {
        // 获取文章列表
        getList: function(knowledgeBaseId, page = 1, size = 10) {
            return request(`/article/list/${knowledgeBaseId}?page=${page}&size=${size}`);
        },

        // 获取最新文章
        getLatest: (page = 1, size = 10) => {
            return request(`/article/recent?page=${page}&size=${size}`);
        },

        getPopular: function(page = 1, size = 10) {
            return request(`/article/popular?page=${page}&size=${size}`);
        },

        // 获取文章详情
        getDetail: function(id) {
            return request(`/article/${id}`);
        },


        getRecent: function(page = 1, size = 10, sortBy = 'createTime') {
            return request(`/article/recent?page=${page}&size=${size}&sortBy=${sortBy}`);
        },

        // 创建文章
        create: function(knowledgeBaseId, title, content) {
            return request('/article', 'POST', {
                knowledgeBaseId: knowledgeBaseId,
                title: title,
                content: content
            });
        },

        // 更新文章
        update: function(id, title, content) {
            return request(`/article/${id}`, 'PUT', {
                id: id,
                title: title,
                content: content
            });
        },

        // 删除文章
        delete: function(id) {
            return request(`/article/${id}`, 'DELETE');
        },

        // 点赞文章
        like: function(id) {
            return request(`/article/like/${id}`, 'POST');
        },

        // 取消点赞
        unlike: function(id) {
            return request(`/article/unlike/${id}`, 'POST');
        },

        favorite: function(id) {
            return request(`/article/favorite/${id}`, 'POST');
        },

        unfavorite: function(id) {
            return request(`/article/unfavorite/${id}`, 'POST');
        }
    },

    // 评论相关API
    comment: {
        // 获取文章评论列表
        getList: function(articleId, page = 1, size = 10) {
            return request(`/comment/article/${articleId}?page=${page}&size=${size}`);
        },

        add: function(articleId, content) {
            return request('/comment', 'POST', {
                articleId: articleId,
                content: content
            });
        },

        update: function(id, content) {
            return request(`/comment/${id}`, 'PUT', {
                content: content
            });
        },

        delete: function(id) {
            return request(`/comment/${id}`, 'DELETE');
        }
    },

    // 企业相关API
    enterprise: {
        // 获取我的企业列表
        getMyList: () => {
            return request('/enterprise/list');
        },

        // 获取企业详情
        getDetail: (id) => {
            return request(`/enterprise/${id}`);
        },

        // 创建企业
        create: (name) => {
            return request('/enterprise', 'POST', { name });
        },

        // 更新企业
        update: (id, name) => {
            return request(`/enterprise/${id}`, 'PUT', { name });
        },

        // 删除企业
        delete: (id) => {
            return request(`/enterprise/${id}`, 'DELETE');
        }
    },

    // 搜索相关API
    search: {
        // 全局搜索
        global: (keyword) => {
            return request(`/search?keyword=${encodeURIComponent(keyword)}`);
        }
    },

    // 浏览历史相关API
    browseHistory: {
        // 获取浏览历史
        getList: () => {
            return request('/browse-history');
        },

        // 删除单条浏览历史
        delete: (id) => {
            return request(`/browse-history/${id}`, 'DELETE');
        },

        // 清空浏览历史
        clear: () => {
            return request('/browse-history/clear', 'POST');
        }
    },

    // 用户关系相关API
    userRelation: {
        // 获取关注列表
        getFollowing: () => {
            return request('/user/following');
        },

        // 获取粉丝列表
        getFollowers: () => {
            return request('/user/followers');
        },

        // 关注用户
        follow: (userId) => {
            return request('/user/follow', 'POST', { userId });
        },

        // 取消关注
        unfollow: (userId) => {
            return request('/user/unfollow', 'POST', { userId });
        }
    },

    user: {
        getCurrentUser: function() {
            return request('/user/current');
        },

        getProfile: function(id) {
            return request(`/user/${id}`);
        },

        updateProfile: function(profileData) {
            return request('/user/profile', 'PUT', profileData);
        },

        changePassword: function(currentPassword, newPassword) {
            return request('/user/password', 'PUT', {
                currentPassword: currentPassword,
                newPassword: newPassword
            });
        },

        follow: function(userId) {
            return request('/user/follow', 'POST', { userId: userId });
        },

        unfollow: function(userId) {
            return request('/user/unfollow', 'POST', { userId: userId });
        }
    },
};

// 导出API服务
window.ApiService = ApiService;