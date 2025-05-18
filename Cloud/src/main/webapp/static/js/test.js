/**
 * 认证服务脚本
 * 处理用户注册、登录、找回密码等认证相关功能
 */

// 认证对象
const Auth = {
    // 登录函数
    login: function() {
        const btn = $('#loginForm button[type="submit"]');
        btn.prop('disabled', true).text('登录中...');

        const data = {
            username: $('#username').val().trim(),
            password: $('#password').val().trim(),
            captcha: $('#regCaptcha').val().trim(),
            captchaToken: $('#regCaptchaToken').val().trim()
        };

        // 基础验证
        if (!data.username || data.username.length < 4) {
            UIController.showToast('用户名至少4个字符', 'error');
            btn.prop('disabled', false).text('登录');
            return;
        }
        if (!data.password || data.password.length < 8) {
            UIController.showToast('密码至少8个字符', 'error');
            btn.prop('disabled', false).text('登录');
            return;
        }
        if (!data.captcha || data.captcha.length !== 4) {
            UIController.showToast('请输入4位图形验证码', 'error');
            btn.prop('disabled', false).text('登录');
            return;
        }

        ApiService.auth.login(data.username, data.password, data.captcha, data.captchaToken)
            .then(res => {
                if (res.code === 200) {
                    UIController.showToast('登录成功', 'success');

                    // 存储用户信息
                    if (res.data && res.data.user) {
                        localStorage.setItem('currentUser', JSON.stringify(res.data.user));
                    }

                    // 延迟跳转，让用户看到成功提示
                    setTimeout(() => {
                        window.location.href = '/Cloud/knowledge.html';
                    }, 1000);
                } else {
                    UIController.showToast(res.message || '登录失败', 'error');
                    Auth.refreshRegCaptcha();
                    btn.prop('disabled', false).text('登录');
                }
            })
            .catch(err => {
                console.error('登录失败:', err);
                UIController.showToast('登录失败，请稍后重试', 'error');
                Auth.refreshRegCaptcha();
                btn.prop('disabled', false).text('登录');
            });
    },

    // 注册函数
    register: function() {
        const btn = $('#registerForm button[type="submit"]');
        btn.prop('disabled', true).text('注册中...');

        const data = {
            username: $('#regUsername').val().trim(),
            password: $('#regPassword').val().trim(),
            email: $('#regEmail').val().trim(),
            emailCode: $('#emailCode').val().trim(),
            captcha: $('#regCaptcha').val().trim()
        };

        // 基础验证
        if (!data.username || data.username.length < 4) {
            UIController.showToast('用户名至少4个字符', 'error');
            btn.prop('disabled', false).text('注册');
            return;
        }
        if (!data.password || data.password.length < 8) {
            UIController.showToast('密码至少8个字符', 'error');
            btn.prop('disabled', false).text('注册');
            return;
        }
        if (!data.email || !/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(data.email)) {
            UIController.showToast('请输入有效的邮箱地址', 'error');
            btn.prop('disabled', false).text('注册');
            return;
        }
        if (!data.emailCode || data.emailCode.length !== 6) {
            UIController.showToast('请输入6位邮箱验证码', 'error');
            btn.prop('disabled', false).text('注册');
            return;
        }
        if (!data.captcha || data.captcha.length !== 4) {
            UIController.showToast('请输入4位图形验证码', 'error');
            btn.prop('disabled', false).text('注册');
            return;
        }

        // 密码复杂度验证
        if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,20}$/.test(data.password)) {
            UIController.showToast('密码必须包含大小写字母、数字和特殊符号', 'error');
            btn.prop('disabled', false).text('注册');
            return;
        }

        ApiService.auth.register(data.username, data.password, data.email, data.emailCode, data.captcha)
            .then(res => {
                if (res.code === 200) {
                    UIController.showToast('注册成功，请登录', 'success');

                    // 延迟跳转，让用户看到成功提示
                    setTimeout(() => {
                        window.location.href = '/Cloud/login.html';
                    }, 1500);
                } else {
                    UIController.showToast(res.message || '注册失败', 'error');
                    Auth.refreshRegCaptcha();
                    btn.prop('disabled', false).text('注册');
                }
            })
            .catch(err => {
                console.error('注册失败:', err);
                UIController.showToast('注册失败，请稍后重试', 'error');
                Auth.refreshRegCaptcha();
                btn.prop('disabled', false).text('注册');
            });
    },

    // 发送邮箱验证码
    sendEmailCode: function() {
        const email = $('#regEmail').val().trim();
        if (!email) {
            UIController.showToast('请输入邮箱地址', 'error');
            return;
        }

        // 验证邮箱格式
        if (!/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(email)) {
            UIController.showToast('邮箱格式不正确', 'error');
            return;
        }

        // 禁用按钮防止重复点击
        const btn = $('#sendCodeBtn');
        btn.prop('disabled', true);
        let countdown = 60;
        btn.text(countdown + '秒后重试');
        const timer = setInterval(() => {
            countdown--;
            btn.text(countdown + '秒后重试');
            if (countdown <= 0) {
                clearInterval(timer);
                btn.prop('disabled', false);
                btn.text('发送验证码');
            }
        }, 1000);

        ApiService.auth.sendEmailCode(email)
            .then(res => {
                if (res.code === 200) {
                    UIController.showToast('验证码已发送至邮箱', 'success');
                } else {
                    UIController.showToast(res.message || '发送失败', 'error');
                    clearInterval(timer);
                    btn.prop('disabled', false).text('发送验证码');
                }
            })
            .catch(err => {
                console.error('发送验证码失败:', err);
                UIController.showToast('发送失败，请稍后重试', 'error');
                clearInterval(timer);
                btn.prop('disabled', false).text('发送验证码');
            });
    },

    // 发送重置密码验证码
    sendResetCode: function() {
        const email = $('#resetEmail').val().trim();
        if (!email) {
            UIController.showToast('请输入邮箱地址', 'error');
            return;
        }

        // 验证邮箱格式
        if (!/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(email)) {
            UIController.showToast('邮箱格式不正确', 'error');
            return;
        }

        // 禁用按钮防止重复点击
        const btn = $('#sendResetCodeBtn');
        btn.prop('disabled', true);
        let countdown = 60;
        btn.text(countdown + '秒后重试');
        const timer = setInterval(() => {
            countdown--;
            btn.text(countdown + '秒后重试');
            if (countdown <= 0) {
                clearInterval(timer);
                btn.prop('disabled', false);
                btn.text('发送验证码');
            }
        }, 1000);

        // 显示加载状态
        UIController.showLoading();

        // 发送请求
        ApiService.auth.sendResetCode(email)
            .then(res => {
                UIController.hideLoading();
                if (res.code === 200) {
                    UIController.showToast('验证码已发送至邮箱', 'success');
                } else {
                    UIController.showToast(res.message || '发送失败', 'error');
                    clearInterval(timer);
                    btn.prop('disabled', false).text('发送验证码');
                }
            })
            .catch(err => {
                UIController.hideLoading();
                UIController.showToast('发送失败，请稍后重试', 'error');
                console.error('发送重置密码验证码失败:', err);
                clearInterval(timer);
                btn.prop('disabled', false).text('发送验证码');
            });
    },

    // 重置密码
    resetPassword: function() {
        const email = $('#resetEmail').val().trim();
        const code = $('#resetCode').val().trim();
        const password = $('#resetPassword').val().trim();
        const confirmPassword = $('#resetConfirmPassword').val().trim();

        // 表单验证
        if (!email || !/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(email)) {
            UIController.showToast('请输入有效的邮箱地址', 'error');
            return;
        }
        if (!code || code.length !== 6) {
            UIController.showToast('请输入6位验证码', 'error');
            return;
        }
        if (!password || password.length < 8) {
            UIController.showToast('密码至少8个字符', 'error');
            return;
        }
        if (password !== confirmPassword) {
            UIController.showToast('两次输入的密码不一致', 'error');
            return;
        }

        // 密码复杂度验证
        if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,20}$/.test(password)) {
            UIController.showToast('密码必须包含大小写字母、数字和特殊符号', 'error');
            return;
        }

        // 显示加载状态
        UIController.showLoading();

        // 调用重置密码API
        ApiService.auth.resetPassword(email, code, password)
            .then(res => {
                UIController.hideLoading();
                if (res.code === 200) {
                    UIController.showToast('密码重置成功，请使用新密码登录', 'success');

                    // 延迟跳转到登录页面
                    setTimeout(() => {
                        window.location.href = '/Cloud/login.html';
                    }, 2000);
                } else {
                    UIController.showToast(res.message || '密码重置失败', 'error');
                }
            })
            .catch(err => {
                UIController.hideLoading();
                UIController.showToast('密码重置失败，请稍后重试', 'error');
                console.error('重置密码失败:', err);
            });
    },

    // 刷新注册验证码
    refreshRegCaptcha: function() {
        UIController.showLoading();

        ApiService.auth.getCaptcha()
            .then(res => {
                if (res.code === 200 && res.data) {
                    $('#regCaptchaImg').attr('src', res.data.image);
                    $('#regCaptchaToken').val(res.data.token);
                } else {
                    console.error('获取验证码失败:', res.message);
                    UIController.showToast('获取验证码失败', 'error');
                }
            })
            .catch(err => {
                console.error('获取验证码失败:', err);
                UIController.showToast('获取验证码失败', 'error');
            })
            .finally(() => {
                UIController.hideLoading();
            });
    },

    // 检查用户名是否可用
    checkUsernameAvailable: function(username) {
        if (username.length < 4) {
            $('#usernameTip').text('用户名至少4个字符').css('color', 'red');
            return;
        }

        ApiService.auth.checkUsername(username)
            .then(res => {
                if (res.code === 200) {
                    $('#usernameTip').text('用户名可用').css('color', 'green');
                } else {
                    $('#usernameTip').text(res.message || '用户名已被使用').css('color', 'red');
                }
            })
            .catch(err => {
                console.error('检查用户名失败:', err);
                $('#usernameTip').text('检查用户名失败').css('color', 'red');
            });
    },

    // 退出登录
    logout: function() {
        UIController.showLoading();

        ApiService.auth.logout()
            .then(res => {
                // 不管成功失败都清除本地存储
                localStorage.removeItem('currentUser');

                // 跳转到登录页面
                window.location.href = '/Cloud/login.html';
            })
            .catch(err => {
                console.error('退出登录失败:', err);

                // 出错也清除本地存储并跳转
                localStorage.removeItem('currentUser');
                window.location.href = '/Cloud/login.html';
            })
            .finally(() => {
                UIController.hideLoading();
            });
    },

    // 初始化忘记密码页面
    initForgotPasswordPage: function() {
        // 绑定发送验证码按钮事件
        $('#sendResetCodeBtn').off('click').on('click', this.sendResetCode);

        // 绑定重置密码表单提交事件
        $('#resetPasswordForm').off('submit').on('submit', function(e) {
            e.preventDefault();
            Auth.resetPassword();
        });

    }
};

// 导出模块
window.Auth = Auth;

