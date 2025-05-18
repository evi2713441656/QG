/**
 * 主应用初始化脚本
 * 处理应用启动和页面初始化
 */

// DOM加载完成时执行
document.addEventListener('DOMContentLoaded', () => {
    // 检查当前页面
    initCurrentPage();

    // 绑定全局事件
    bindGlobalEvents();

    // 检查用户登录状态
    checkLoginStatus();
});

// 初始化当前页面
function initCurrentPage() {
    // 获取当前路径
    const path = window.location.pathname;
    const filename = path.split('/').pop();

    // 根据不同页面执行不同的初始化
    switch (filename) {
        case 'login.html':
            initLoginPage();
            break;
        case 'register.html':
            initRegisterPage();
            break;
        case 'forgot-password.html':
            Auth.initForgotPasswordPage();
            break;
        case 'knowledge.html':
            KnowledgeManager.initKnowledgePage();
            break;
        default:
            // 如果是首页或其他页面，检查登录状态
            checkLoginStatus();
            break;
    }
}

// 初始化登录页面
function initLoginPage() {
    // 绑定登录表单提交事件
    document.getElementById('loginForm').addEventListener('submit', function(e) {
        e.preventDefault();
        Auth.login();
    });

    // 初始化验证码
    Auth.refreshRegCaptcha();
}

// 初始化注册页面
function initRegisterPage() {
    // 绑定注册表单提交事件
    document.getElementById('registerForm').addEventListener('submit', function(e) {
        e.preventDefault();
        Auth.register();
    });

    // 绑定用户名检查事件
    document.getElementById('regUsername').addEventListener('input', function() {
        Auth.checkUsernameAvailable(this.value);
    });

    // 绑定发送验证码按钮事件
    document.getElementById('sendCodeBtn').addEventListener('click', Auth.sendEmailCode);

    // 初始化验证码
    Auth.refreshRegCaptcha();
}

// 绑定全局事件
function bindGlobalEvents() {
    // 绑定退出登录事件
    const logoutLinks = document.querySelectorAll('.logout-link');
    logoutLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            Auth.logout();
        });
    });

    // 绑定页面切换事件
    document.querySelectorAll('[data-page]').forEach(element => {
        element.addEventListener('click', function(e) {
            e.preventDefault();
            const page = this.dataset.page;
            UIController.showPage(page);
        });
    });

    // 绑定用户菜单切换事件
    document.querySelectorAll('.user-avatar').forEach(avatar => {
        avatar.addEventListener('click', function() {
            toggleUserMenu();
        });
    });

    // 点击页面其他地方关闭用户菜单
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.user-avatar') && !e.target.closest('.user-menu')) {
            document.querySelectorAll('.user-menu').forEach(menu => {
                menu.classList.add('hidden');
            });
        }
    });

    // 绑定搜索框事件
    const searchInputs = document.querySelectorAll('.search-input');
    searchInputs.forEach(input => {
        input.addEventListener('input', debounce(function() {
            if (window.KnowledgeManager && typeof window.KnowledgeManager.search === 'function') {
                window.KnowledgeManager.search();
            }
        }, 500));
    });
}

// 检查用户登录状态
function checkLoginStatus() {
    // 尝试从本地存储获取用户信息
    const userInfo = localStorage.getItem('currentUser');

    // 如果没有登录信息，跳转到登录页面
    // 排除登录、注册和忘记密码页面
    const path = window.location.pathname;
    const filename = path.split('/').pop();

    if (!userInfo && !['login.html', 'register.html', 'forgot-password.html'].includes(filename)) {
        // 跳转到登录页面
        window.location.href = 'login.html';
        return;
    }

    // 如果已登录但处于登录、注册或忘记密码页面，跳转到首页
    if (userInfo && ['login.html', 'register.html', 'forgot-password.html'].includes(filename)) {
        window.location.href = 'knowledge.html';
        return;
    }

    // 如果已登录，更新UI展示用户信息
    if (userInfo) {
        try {
            const user = JSON.parse(userInfo);
            updateUserUI(user);
        } catch (error) {
            console.error('解析用户信息失败:', error);
            // 清除可能损坏的存储
            localStorage.removeItem('currentUser');
            // 跳转到登录页面
            window.location.href = 'login.html';
        }
    }
}

// 更新UI展示用户信息
function updateUserUI(user) {
    // 设置用户头像
    document.querySelectorAll('.user-avatar').forEach(avatar => {
        avatar.src = user.avatar || 'https://picsum.photos/30/30';
    });

    // 设置用户名
    document.querySelectorAll('.user-name').forEach(name => {
        name.textContent = user.username;
    });
}

// 切换用户菜单显示状态
function toggleUserMenu() {
    const menus = document.querySelectorAll('.user-menu');
    menus.forEach(menu => {
        menu.classList.toggle('hidden');
    });
}

// 防抖函数
function debounce(func, wait) {
    let timeout;
    return function() {
        const context = this;
        const args = arguments;
        clearTimeout(timeout);
        timeout = setTimeout(() => {
            func.apply(context, args);
        }, wait);
    };
}
