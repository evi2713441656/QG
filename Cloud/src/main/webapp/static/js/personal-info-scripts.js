/**
 * 个人信息管理脚本
 * 处理用户个人信息的查询、修改等功能
 */

// 初始化个人信息页面
function initPersonalInfoPage() {
    // 加载用户信息
    loadUserInfo();

    // 加载浏览记录
    loadBrowseHistory();

    // 绑定编辑按钮事件
    document.getElementById('editInfoBtn').addEventListener('click', enableInfoEdit);

    // 绑定更改头像按钮事件
    document.getElementById('changeAvatarBtn').addEventListener('click', changeAvatar);
}

// 加载用户信息
function loadUserInfo() {
    // 显示加载状态
    UIController.showLoading();

    // 调用获取用户信息API
    ApiService.auth.getCurrentUser()
        .then(res => {
            if (res.code === 200 && res.data) {
                // 填充表单
                document.getElementById('username').value = res.data.username || '';
                document.getElementById('email').value = res.data.email || '';

                // 计算邮箱用户名部分
                if (res.data.email) {
                    const emailParts = res.data.email.split('@');
                    document.getElementById('emailUsername').value = emailParts[0] || '';
                }

                // 设置头像
                if (res.data.avatar) {
                    document.getElementById('userAvatar').src = res.data.avatar;
                }

                // 存储用户信息
                localStorage.setItem('currentUser', JSON.stringify(res.data));
            } else {
                UIController.showToast(res.message || '加载用户信息失败', 'error');
            }
        })
        .catch(err => {
            UIController.showToast(err.message || '加载用户信息失败', 'error');
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// 启用信息编辑
function enableInfoEdit() {
    // 启用表单字段编辑
    document.getElementById('username').readOnly = false;
    document.getElementById('email').readOnly = false;

    // 显示保存和取消按钮
    document.getElementById('saveBtn').classList.remove('hidden');
    document.getElementById('cancelBtn').classList.remove('hidden');

    // 隐藏编辑按钮
    document.getElementById('editInfoBtn').classList.add('hidden');
}

// // 取消编辑
// function cancelPersonalInfo() {
//     // 重新加载用户信息
//     loadUserInfo();
//
//     // 恢复为只读状态
//     disableInfoEdit();
// }

// 禁用信息编辑
function disableInfoEdit() {
    // 禁用表单字段编辑
    document.getElementById('username').readOnly = true;
    document.getElementById('email').readOnly = true;
    document.getElementById('emailUsername').readOnly = true;

    // 隐藏保存和取消按钮
    document.getElementById('saveBtn').classList.add('hidden');
    document.getElementById('cancelBtn').classList.add('hidden');

    // 显示编辑按钮
    document.getElementById('editInfoBtn').classList.remove('hidden');
}


function changeAvatar() {
    const input = document.createElement('input');
    const email = document.getElementById('email').value;
    input.type = 'file';
    input.accept = 'image/*';

    input.onchange = function() {
        if (this.files && this.files[0]) {
            const file = this.files[0];

            // 校验文件大小
            if (file.size > 2 * 1024 * 1024) {
                alert('图片大小不能超过2MB');
                return;
            }

            const formData = new FormData();
            formData.append('avatar', file);
            formData.append('email', email);

            // 发送请求
            fetch('/Cloud/modify/upload-avatar', {
                method: 'POST',
                body: formData
            })
                .then(async response => {
                    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
                    return response.json();
                })
                .then(data => {
                    if (data.code === 200) {
                        const avatarUrl = data.data.url || data.data;
                        document.getElementById('userAvatar').src = avatarUrl + '?t=' + Date.now(); // 加时间戳避免缓存
                        alert('头像更新成功');
                        // 更新头像显示
                        document.getElementById('userAvatar').src = data.data.url || data.data;
                    } else {
                        alert(data.message || '头像上传失败');
                    }
                })
                .catch(error => {
                    console.error('头像上传失败:', error);
                    alert('头像上传失败，请稍后重试');
                });
        }
    };

    input.click();
}

// 显示修改密码模态框
function showChangePasswordModal() {
    document.getElementById('change-password-modal').classList.remove('hidden');
}

// 隐藏修改密码模态框
function hideChangePasswordModal() {
    document.getElementById('change-password-modal').classList.add('hidden');

    // 清空表单
    document.getElementById('current-password').value = '';
    document.getElementById('new-password').value = '';
    document.getElementById('confirm-password').value = '';
}

// 修改 personal-info-scripts.js 中的 submitChangePassword 函数
function submitChangePassword() {
    // 获取表单数据
    const oldPassword = document.getElementById('current-password').value;
    const newPassword = document.getElementById('new-password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    const email = document.getElementById('email').value;


    // 表单验证
    if (!oldPassword) {
        alert('请输入当前密码');
        return;
    }

    if (!newPassword) {
        alert('请输入新密码');
        return;
    }

    if (newPassword !== confirmPassword) {
        alert('两次输入的密码不一致');
        return;
    }

    // 创建请求数据
    const requestData = {
        oldPassword: oldPassword,
        newPassword: newPassword,
        email: email,
    };

    // 发送请求
    fetch('/Cloud/update-password', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
    })
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                alert('密码修改成功');
                // 关闭模态框
                hideChangePasswordModal();
            } else {
                alert(data.message || '密码修改失败');
            }
        })
        .catch(error => {
            console.error('密码修改失败:', error);
            alert('密码修改失败，请稍后重试');
        });
}

// 加载关注列表
function loadFollowingList() {
    ApiService.userRelation.getFollowing()
        .then(res => {
            if (res.code === 200 && res.data) {
                // 获取关注列表容器
                const container = document.getElementById('following-list');
                container.innerHTML = '';

                // 如果没有关注，显示提示
                if (res.data.length === 0) {
                    container.innerHTML = '<div class="text-center text-gray-500 p-4">暂无关注的用户</div>';
                    return;
                }

                // 渲染关注列表
                const grid = document.createElement('div');
                grid.className = 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4';

                res.data.forEach(user => {
                    grid.appendChild(createUserCard(user));
                });

                container.appendChild(grid);
            } else {
                UIController.showToast(res.message || '加载关注列表失败', 'error');
            }
        })
        .catch(err => {
            UIController.showToast(err.message || '加载关注列表失败', 'error');
        });
}

// 创建用户卡片
function createUserCard(user) {
    const card = document.createElement('div');
    card.className = 'user-card bg-white shadow-md rounded p-4 flex items-center';
    card.onclick = () => showUserProfile(user.id);

    // 设置卡片内容
    card.innerHTML = `
    <img src="${user.avatar || 'https://picsum.photos/30/30'}" alt="用户头像" class="rounded-full w-12 h-12 mr-4">
    <div class="flex-1">
      <h4 class="text-md font-bold">${user.username}</h4>
      <p class="text-gray-600 text-sm">关注: ${user.followingCount || 0} | 粉丝: ${user.followerCount || 0}</p>
    </div>
    <button class="unfollow-btn bg-red-500 text-white px-2 py-1 rounded text-sm" onclick="unfollowUser(event, ${user.id})">
      取消关注
    </button>
  `;

    return card;
}

// 取消关注用户
function unfollowUser(event, userId) {
    // 阻止事件冒泡
    event.stopPropagation();

    // 确认取消关注
    UIController.showConfirm('确定要取消关注该用户吗？', () => {
        ApiService.userRelation.unfollow(userId)
            .then(res => {
                if (res.code === 200) {
                    UIController.showToast('取消关注成功', 'success');

                    // 重新加载关注列表
                    loadFollowingList();
                } else {
                    UIController.showToast(res.message || '取消关注失败', 'error');
                }
            })
            .catch(err => {
                UIController.showToast(err.message || '取消关注失败', 'error');
            });
    });
}

// 显示用户个人主页
function showUserProfile(userId) {
    // 跳转到用户个人主页
    window.location.href = `/user-profile.html?id=${userId}`;
}

// // 提交修改密码
// function submitChangePassword() {
//     // 获取表单数据
//     const currentPassword = document.getElementById('current-password').value;
//     const newPassword = document.getElementById('new-password').value;
//     const confirmPassword = document.getElementById('confirm-password').value;
//
//     // 表单验证
//     if (!currentPassword) {
//         UIController.showToast('请输入当前密码', 'error');
//         return;
//     }
//
//     if (!newPassword) {
//         UIController.showToast('请输入新密码', 'error');
//         return;
//     }
//
//     if (newPassword !== confirmPassword) {
//         UIController.showToast('两次输入的密码不一致', 'error');
//         return;
//     }
//
//     // 密码复杂度验证
//     if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,20}$/.test(newPassword)) {
//         UIController.showToast('密码必须包含大小写字母、数字和特殊符号，长度8-20位', 'error');
//         return;
//     }
//
//     // 显示加载状态
//     UIController.showLoading();
//
//     // 调用修改密码API
//     fetch('/password/update', {
//         method: 'POST',
//         headers: {
//             'Content-Type': 'application/json'
//         },
//         body: JSON.stringify({
//             currentPassword,
//             newPassword
//         })
//     })
//         .then(response => response.json())
//         .then(data => {
//             if (data.code === 200) {
//                 UIController.showToast('密码修改成功', 'success');
//
//                 // 关闭模态框
//                 hideChangePasswordModal();
//             } else {
//                 UIController.showToast(data.message || '密码修改失败', 'error');
//             }
//         })
//         .catch(err => {
//             UIController.showToast('密码修改失败', 'error');
//             console.error('密码修改失败:', err);
//         })
//         .finally(() => {
//             UIController.hideLoading();
//         });
// }
