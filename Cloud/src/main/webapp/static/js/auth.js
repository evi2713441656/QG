// 全局配置
const API_BASE_URL = 'http://localhost:8081/Cloud';

// 登录函数
function login() {
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
        alert('用户名至少4个字符');
        btn.prop('disabled', false).text('登录');
        return;
    }
    if (!data.password || data.password.length < 8) {
        alert('密码至少8个字符');
        btn.prop('disabled', false).text('登录');
        return;
    }
    if (!data.captcha || data.captcha.length !== 4) {
        alert('请输入4位图形验证码');
        btn.prop('disabled', false).text('登录');
        return;
    }


    $.ajax({
        url: API_BASE_URL+ '/login',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function(res) {
            if (res.code === 200) {
                window.location.href = '/Cloud/knowledge.html'; // 登录成功后跳转
            } else {
                alert(res.message);
                refreshCaptcha();
                btn.prop('disabled', false).text('登录');
            }
        },
        error: function(xhr) {
            console.log('登录失败，状态码: ', xhr.status);
            console.log('登录失败，响应文本: ', xhr.responseText);
            let errorMsg = '登录失败';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMsg += ': ' + xhr.responseJSON.message;
            }
            alert(errorMsg);
            refreshCaptcha();
            btn.prop('disabled', false).text('登录');
        }
    });
}

// 注册函数
function register() {
    const btn = $('#registerForm button[type="submit"]');
    btn.prop('disabled', true).text('注册中...');

    const data = {
        username: $('#regUsername').val().trim(),
        password: $('#regPassword').val().trim(),
        email: $('#regEmail').val().trim(),
        emailCode: $('#emailCode').val().trim(),
        // captcha: $('#regCaptcha').val().trim()
    };

    // 基础验证
    if (!data.username || data.username.length < 4) {
        alert('用户名至少4个字符');
        btn.prop('disabled', false).text('注册');
        return;
    }
    if (!data.password || data.password.length < 8) {
        alert('密码至少8个字符');
        btn.prop('disabled', false).text('注册');
        return;
    }
    if (!data.email || !/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(data.email)) {
        alert('请输入有效的邮箱地址');
        btn.prop('disabled', false).text('注册');
        return;
    }
    if (!data.emailCode || data.emailCode.length !== 6) {
        alert('请输入6位邮箱验证码');
        btn.prop('disabled', false).text('注册');
        return;
    }
    // if (!data.captcha || data.captcha.length !== 4) {
    //     alert('请输入4位图形验证码');
    //     btn.prop('disabled', false).text('注册');
    //     return;
    // }

    $.ajax({
        url: API_BASE_URL + '/register',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (res) {
            if (res.code === 200) {
                alert('注册成功，请登录');
                window.location.href = '/Cloud/login.html';
            } else {
                alert(res.message || '注册失败');
                btn.prop('disabled', false).text('注册');
            }
        },
        error: function (xhr) {
            let errorMsg = '注册失败';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMsg += ': ' + xhr.responseJSON.message;
            }
            alert(errorMsg);
            // refreshCaptcha();
            btn.prop('disabled', false).text('注册');
        }
    });
}

// 刷新验证码
function refreshCaptcha() {
    $.ajax({
        url: API_BASE_URL + '/captcha',
        method: 'GET',
        timeout: 5000, // 设置超时时间为 5 秒
        success: function(res) {
            if (res.success && res.data) {
                $('#regCaptchaImg').attr('src', res.data.image);
                $('#regCaptchaToken').val(res.data.token);
            } else {
                console.error('验证码获取失败:', res.message);
                // setTimeout(refreshCaptcha, 3000); // 3秒后重试
            }
        },
        error: function(xhr) {
            console.error('验证码请求失败: 状态码', xhr.status, '状态文本', xhr.statusText);
            if (xhr.status === 404) {
                console.error('接口未找到，请检查接口地址');
            } else if (xhr.status === 500) {
                console.error('服务器内部错误，请联系管理员');
            }
            // setTimeout(refreshCaptcha, 3000);
        }
    });
}

// 发送邮箱验证码
function sendEmailCode() {
    const email = $('#regEmail').val();
    if (!email) {
        alert('请输入邮箱地址');
        return;
    }

    // 验证邮箱格式
    if (!/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(email)) {
        alert('邮箱格式不正确');
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

    // 发送请求
    $.ajax({
        url: API_BASE_URL + '/email-code',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ email: email }),
        success: function(res) {
            if (res.code === 200) {
                alert("验证码已发送至邮箱");
            } else {
                alert(res.message || "发送失败");
            }
        },
        error: function(xhr) {
            let errorMessage;
            if (xhr && typeof xhr.responseText === 'string') {
                errorMessage = xhr.responseText;
            } else {
                errorMessage = '无法获取服务器响应信息';
            }
            console.error("Error:", errorMessage);
            alert("服务器错误: " + (xhr.responseJSON?.message || xhr.statusText));
        },
        always: function() {
            clearInterval(timer);
            btn.prop('disabled', false).text('发送验证码');
        }
    });
}

// 处理请求结果的通用函数
function handleRequestResult(timer, btn) {
    clearInterval(timer);
    btn.prop('disabled', false);
    btn.text('发送验证码');
}

function checkUsernameAvailable(username) {
    if (username.length < 4) {
        $('#usernameTip').text('用户名至少4个字符').css('color', 'red');
        return;
    }

    $.get(API_BASE_URL + '/check-username?username=' + encodeURIComponent(username))
        .done(function(res) {
            // 检查响应数据格式
            if (typeof res === 'object' && res.hasOwnProperty('code') && res.hasOwnProperty('message')) {
                if (res.code === 200) {
                    $('#usernameTip').text('用户名可用').css('color', 'green');
                } else {
                    $('#usernameTip').text(res.message).css('color', 'red');
                }
            } else {
                $('#usernameTip').text('服务器返回数据格式错误').css('color', 'red');
            }
        })
        .fail(function(jqXHR, textStatus, errorThrown) {
            // 处理请求失败的情况
            $('#usernameTip').text('请求出错: ' + textStatus).css('color', 'red');
        });
}

