// Global pagination variables for knowledge bases
let currentKnowledgePage = 1;
let knowledgeBasesPerPage = 9;
let totalKnowledgePages = 1;

// 加载知识库列表
function loadKnowledgeBases() {
    $.get('/Cloud/knowledge/list/my', function(res) {
        if (res.code === 200) {
            renderKnowledgeList(res.data);
        } else {
            alert(res.message);
        }
    }).fail(function() {
        alert('加载失败，请刷新重试');
    });
}

document.addEventListener('DOMContentLoaded', function() {
    // Initialize knowledge page
    initKnowledgePage();

    // Bind sort select change event
    const sortSelect = document.getElementById('sort-select');
    if (sortSelect) {
        sortSelect.addEventListener('change', sortArticles);
    }

    // Load articles with pagination
    const articlesContainer = document.getElementById('recent-articles');
    if (articlesContainer) {
        loadArticles(1, 'createTime');
    }

    // Load public knowledge bases with pagination
    const publicKnowledgeBases = document.getElementById('public-knowledgebases');
    if (publicKnowledgeBases && window.location.href.includes('publicKnowledgeBasesPage')) {
        loadPublicKnowledgeBases(1, 9);
    }
});

window.onload = function() {
    // Hide loading indicator
    const loadingElement = document.getElementById('loading');
    if (loadingElement) {
        loadingElement.classList.add('hidden');
    }

    // Initialize knowledge page
    initKnowledgePage();
};

// 在DOM加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    console.log("DOM加载完成，初始化UI控制器");

    // 检查加载指示器
    setTimeout(function() {
        UIController.hideLoading();
    }, 1000);
});

// 在knowledge.js文件开头添加
window.onload = function() {
    // 5秒后强制隐藏加载指示器，无论发生什么
    setTimeout(function() {
        const loadingElement = document.getElementById('loading');
        if (loadingElement) {
            loadingElement.classList.add('hidden');
            console.log("强制隐藏加载指示器");
        }
    }, 5000);
};

// 格式化文本编辑器内容
function applyFormat(command) {
    document.execCommand(command, false, null);
    document.getElementById('editor-content').focus();
}

// 上传图片
function uploadImage() {
    // 创建文件选择器
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';

    // 绑定文件选择事件
    input.onchange = function() {
        if (this.files && this.files[0]) {
            const file = this.files[0];

            // 创建FormData对象
            const formData = new FormData();
            formData.append('file', file);

            // 显示加载状态
            UIController.showLoading();

            // 上传图片
            fetch('/upload/image', {
                method: 'POST',
                body: formData
            })
                .then(response => response.json())
                .then(data => {
                    if (data.code === 200) {
                        // 插入图片
                        document.execCommand('insertImage', false, data.data.url);
                        UIController.showToast('图片上传成功', 'success');
                    } else {
                        UIController.showToast(data.message || '图片上传失败', 'error');
                    }
                })
                .catch(err => {
                    UIController.showToast('图片上传失败', 'error');
                    console.error('图片上传失败:', err);
                })
                .finally(() => {
                    UIController.hideLoading();
                });
        }
    };

    // 触发文件选择
    input.click();
}

/**
 * 加载指示器修复脚本
 * 解决加载指示器无法隐藏的问题
 */

// 页面加载完成后立即执行
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM加载完成，准备隐藏加载指示器');

    // 立即尝试隐藏加载指示器
    hideLoadingIndicator();

    // 再次尝试使用UIController隐藏
    if (UIController && typeof UIController.hideLoading === 'function') {
        console.log('使用UIController隐藏加载指示器');
        UIController.hideLoading();
    }

    // 设置一个定时器，以确保无论如何都会隐藏加载指示器
    setTimeout(hideLoadingIndicator, 1000);

    // 最后的保险措施 - 5秒后强制隐藏
    setTimeout(function() {
        console.log('强制隐藏加载指示器');
        const loadingElement = document.getElementById('loading');
        if (loadingElement) {
            loadingElement.style.display = 'none';
            loadingElement.classList.add('hidden');
        }
    }, 5000);
});

document.addEventListener('DOMContentLoaded', function() {
    initKnowledgePage();
});

// 隐藏加载指示器的函数
function hideLoadingIndicator() {
    console.log('尝试隐藏加载指示器');
    const loadingElement = document.getElementById('loading');
    if (loadingElement) {
        loadingElement.classList.add('hidden');
        console.log('加载指示器已隐藏');
    } else {
        console.error('未找到加载指示器元素');
    }
}

// 监听window的load事件
window.addEventListener('load', function() {
    console.log('页面完全加载完成，再次尝试隐藏加载指示器');
    hideLoadingIndicator();
});

// 监听错误
window.addEventListener('error', function(event) {
    console.error('检测到页面错误，但仍尝试隐藏加载指示器:', event.message);
    hideLoadingIndicator();
});

// 渲染知识库列表
function renderKnowledgeList(bases) {
    const $container = $('#knowledgeList');
    $container.empty();

    if (bases.length === 0) {
        $container.html('<div class="col-12"><div class="alert alert-info">暂无知识库，点击右上角创建</div></div>');
        return;
    }

    bases.forEach(base => {
        const card = `
        <div class="col-md-4 mb-4">
            <div class="card knowledge-card">
                <div class="card-body">
                    <h5 class="card-title">${base.name}</h5>
                    <p class="card-text text-muted">${base.description || '暂无描述'}</p>
                    <div class="d-flex justify-content-between">
                        <span class="badge ${base.public ? 'badge-success' : 'badge-secondary'}">
                            ${base.public ? '公开' : '私有'}
                        </span>
                        <a href="/knowledge/detail.html?id=${base.id}" class="btn btn-sm btn-outline-primary">进入</a>
                    </div>
                </div>
                <div class="card-footer text-muted">
                    创建于 ${new Date(base.createTime).toLocaleDateString()}
                </div>
            </div>
        </div>`;
        $container.append(card);
    });
}

// 创建知识库
function createKnowledge() {
    const data = {
        name: $('#kbName').val(),
        description: $('#kbDesc').val(),
        isPublic: $('#kbPublic').is(':checked')
    };

    if (!data.name) {
        alert('请输入知识库名称');
        return;
    }

    $.ajax({
        url: '/knowledge',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function(res) {
            if (res.code === 200) {
                $('#createModal').modal('hide');
                loadKnowledgeBases();
            } else {
                alert(res.message);
            }
        },
        error: function(xhr) {
            alert('创建失败: ' + (xhr.responseJSON?.message || '服务器错误'));
        }
    });
}

// 添加返回知识库详情页的函数
function backToKnowledgeDetail() {
    // 如果有当前知识库ID，则返回到该知识库详情页
    if (window.currentKnowledgeBaseId) {
        showKnowledgeDetail(window.currentKnowledgeBaseId);
    } else {
        // 否则返回到首页
        showPage('homePage');
    }
}

function showMyKnowledgeBasesPage() {
    // 显示主页面（因为我们将在这个页面上添加我的知识库区域）
    showPage('homePage');

    // 加载并显示我的知识库
    loadMyKnowledgeBases();
}

function showPage(pageId) {
    console.log("Showing page:", pageId);

    // 1. 隐藏所有主要页面内容区域
    const mainContentAreas = [
        'homePage',
        'publicKnowledgeBasesPage',
        'recentArticlesPage',
        'companySpacePage',
        'knowledge-detail',
        'company-member-management',
        'company-notice',
        'article-detail',
        'article-editor',
        'member-management',
        'personal-info'
    ];

    mainContentAreas.forEach(id => {
        const page = document.getElementById(id);
        if (page) page.classList.add('hidden');
    });

    // 2. 特殊逻辑处理
    if (pageId === 'myKnowledgeBases') {
        showMyKnowledgeBasesPage();
        return;
    }

    // 3. 显示目标页面
    const targetPage = document.getElementById(pageId);
    if (targetPage) {
        targetPage.classList.remove('hidden');

        // 确保页面正确重置到顶部
        targetPage.scrollTo(0, 0);

        // 特殊处理文章详情页（保持全屏布局）
        if (pageId === 'article-detail') {
            document.body.style.overflow = 'hidden'; // 禁用主页面滚动
        } else if (pageId === 'publicKnowledgeBasesPage') {
            loadPublicKnowledgeBases(1, 9);
        } else if (pageId === 'member-management' && currentKnowledgeBaseId) {
            loadMembers(currentKnowledgeBaseId);
        } else if (pageId === 'personal-info') {
            loadBrowseHistory();
        } else {
            document.body.style.overflow = ''; // 恢复滚动
        }
    } else {
        console.error("Page not found:", pageId);
    }
}

// 显示我的知识库（从API获取的真实数据）
function displayMyKnowledgeBases(knowledgeBases) {
    // 获取新的显示容器
    const contentArea = document.getElementById('my-knowledgebases');
    if (!contentArea) return;

    // 清空现有内容
    contentArea.innerHTML = '';

    // 添加标题（可以考虑在HTML中直接添加，这里仅为保持一致性）
    const title = document.createElement('h2');
    title.className = 'text-xl font-bold mb-4';
    title.textContent = '我的知识库（私有）';
    contentArea.appendChild(title);

    // 创建知识库网格容器
    const grid = document.createElement('div');
    grid.className = 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mt-4';
    grid.id ='my-knowledgebases-grid';

    // 如果没有知识库，显示提示信息
    if (knowledgeBases.length === 0) {
        const emptyMessage = document.createElement('div');
        emptyMessage.className = 'col-span-3 text-center text-gray-500 p-6 bg-gray-100 rounded';
        emptyMessage.textContent = '你还没有创建任何知识库，点击下方按钮创建第一个知识库！';
        grid.appendChild(emptyMessage);
    } else {
        // 显示所有知识库
        knowledgeBases.forEach(kb => {
            grid.appendChild(createKnowledgeBaseCard(kb));
        });
    }

    contentArea.appendChild(grid);

    // 添加创建知识库按钮（可以考虑在HTML中直接添加，这里仅为保持一致性）
    const createButton = document.createElement('button');
    createButton.className = 'bg-blue-500 text-white px-4 py-2 rounded mt-4';
    createButton.textContent = '创建知识库';
    createButton.onclick = showCreateKnowledgeBaseModal;
    contentArea.appendChild(createButton);
}

function loadPublicKnowledgeBases(page = 1, size = 9) {
    // Show loading state
    UIController.showLoading();

    // Update current page
    currentKnowledgePage = page;

    // Get container
    const container = document.getElementById('public-knowledgebases');
    if (!container) {
        UIController.hideLoading();
        return;
    }

    // Clear container
    container.innerHTML = '';

    // Call API to get public knowledge bases
    fetch(`/Cloud/knowledge/list/public?page=${page}&size=${size}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.code === 200) {
                if (!data.data || data.data.length === 0) {
                    container.innerHTML = '<div class="text-center p-4">暂无公开知识库</div>';
                    return;
                }

                // Render knowledge base cards
                data.data.forEach(kb => {
                    const card = createKnowledgeBaseCard(kb);
                    container.appendChild(card);
                });

                // Calculate total pages
                totalKnowledgePages = Math.ceil((data.total || data.data.length) / size);

                // Generate pagination controls
                generatePagination(data.total || data.data.length, page, size, 'pagination-public', (page) => {
                    loadPublicKnowledgeBases(page, size);
                });
            } else {
                UIController.showToast(data.message || '加载公开知识库失败', 'error');
            }
        })
        .catch(error => {
            console.error('Error loading public knowledge bases:', error);
            UIController.showToast('加载失败: ' + error.message, 'error');
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// 重新实现或完善createKnowledgeBaseCard函数
function createKnowledgeBase() {
    const kbName = document.getElementById('kb-name').value;
    const kbDescription = document.getElementById('kb-description').value;
    const kbAccess = document.getElementById('kb-access').value;

    // 表单验证
    if (!kbName) {
        UIController.showToast('请输入知识库名称', 'error');
        return;
    }

    // 显示加载指示器
    UIController.showLoading();

    // 使用ApiService创建知识库
    ApiService.knowledgeBase.create(kbName, kbDescription, kbAccess === 'public')
        .then(res => {
            // 隐藏加载指示器
            UIController.hideLoading();

            if (res.code === 200) {
                // 显示成功消息
                UIController.showToast('知识库创建成功！', 'success');

                // 关闭模态框
                hideCreateKnowledgeBaseModal();

                // 显示我的知识库页面并刷新列表
                showPage('myKnowledgeBases');
            } else {
                // 显示错误消息
                UIController.showToast(res.message || '创建知识库失败', 'error');
            }
        })
        .catch(err => {
            // 隐藏加载指示器
            UIController.hideLoading();

            // 显示错误消息
            UIController.showToast(err.message || '创建知识库失败', 'error');

            // 为了测试，可以模拟成功创建
            console.log('模拟创建知识库成功');
            hideCreateKnowledgeBaseModal();
            showPage('myKnowledgeBases');
        });
}

function showCreateEnterpriseModal() {
    const modal = document.getElementById('create-enterprise-modal');
    modal.classList.remove('hidden');
}

function hideCreateEnterpriseModal() {
    const modal = document.getElementById('create-enterprise-modal');
    modal.classList.add('hidden');
}

let oldEmail = '';
document.addEventListener('DOMContentLoaded', function () {
    // 获取个人信息
    fetch('/Cloud/getuserinfo')
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error(`请求失败，状态码: ${response.status}`);
            }
        })
        .then(data => {
            document.getElementById('username').value = data.data.username;
            document.getElementById('email').value = data.data.email;
            oldEmail = data.data.email;
            // 密码通常不返回明文显示，这里可根据实际情况处理
            // document.getElementById('password').value = data.data.password;
            // 设置Data URL格式的头像
            // const avatarDataUrl = data.data.avatar;
            // if (avatarDataUrl) {
            //     document.getElementById('userAvatar').src = avatarDataUrl;
            //     console.log('最终图片URL:', avatarDataUrl);
            // }
        })
        .catch(error => console.error('获取个人信息失败:', error));

    // 编辑信息按钮点击事件
    document.getElementById('editInfoBtn').addEventListener('click', function () {
        document.getElementById('username').removeAttribute('readonly');
        document.getElementById('email').removeAttribute('readonly');
        document.getElementById('password').removeAttribute('readonly');
        this.classList.add('hidden');
        document.getElementById('saveBtn').classList.remove('hidden');
        document.getElementById('cancelBtn').classList.remove('hidden');
    });
});

// 获取退出登录按钮
const logoutButton = document.querySelector('#user-menu a:last-child');

// 为退出登录按钮添加点击事件监听器
if (logoutButton) {
    logoutButton.addEventListener('click', function () {
        // 清除本地存储中的用户信息
        localStorage.removeItem('userInfo');

        // 跳转到登录页面
        window.location.href = 'login.html';
    });
}

function savePersonalInfo() {
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    // 发送保存请求到后端
    fetch('/Cloud/saveuserinfo', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, email, oldEmail })
    })
        .then(response => {
            console.log('服务器响应状态:', response.status);
            console.log('服务器响应头:', response.headers);
            // 检查响应状态
            if (response.ok) {
                // 检查响应是否包含 JSON 内容
                const contentType = response.headers.get('content-type');
                if (contentType && contentType.includes('application/json')) {
                    return response.json();
                } else {
                    throw new Error('响应不是 JSON 格式');
                }
            } else {
                // 非 200 状态码的处理
                throw new Error(`请求失败，状态码: ${response.status}`);
            }
        })
        .then(data => {
            console.log('服务器返回的数据:', data);
            if (data.message === '保存成功') {
                alert('个人信息保存成功');
                // 保存成功后恢复到查看模式
                cancelPersonalInfo();
            } else {
                alert('保存失败: ' + data.message);
            }
        })
        .catch(error => {
            console.error('保存个人信息失败:', error);
            alert('保存个人信息失败: ' + error.message);
        });
}

function cancelPersonalInfo() {
    document.getElementById('username').setAttribute('readonly', 'readonly');
    document.getElementById('email').setAttribute('readonly', 'readonly');
    // document.getElementById('password').setAttribute('readonly', 'readonly');
    document.getElementById('editInfoBtn').classList.remove('hidden');
    document.getElementById('saveBtn').classList.add('hidden');
    document.getElementById('cancelBtn').classList.add('hidden');

    // 重新获取个人信息以恢复原始值
    fetch('/Cloud/getuserinfo')
        .then(response => response.json())
        .then(data => {
            document.getElementById('username').value = data.data.username;
            console.log(data.data.username);
            document.getElementById('email').value = data.data.email;
            // 密码通常不返回明文显示，这里可根据实际情况处理
            // document.getElementById('password').value = data.data.password;
        })
        .catch(error => console.error('获取个人信息失败:', error));
}

document.addEventListener('DOMContentLoaded', function() {
    // 绑定编辑信息按钮事件
    const editInfoBtn = document.getElementById('editInfoBtn');
    if (editInfoBtn) {
        editInfoBtn.addEventListener('click', function() {
            document.getElementById('username').readOnly = false;
            document.getElementById('email').readOnly = false;
            document.getElementById('saveBtn').classList.remove('hidden');
            document.getElementById('cancelBtn').classList.remove('hidden');
            this.classList.add('hidden');
        });
    }

    // 绑定更改头像按钮事件
    const changeAvatarBtn = document.getElementById('changeAvatarBtn');
    if (changeAvatarBtn) {
        changeAvatarBtn.addEventListener('click', changeAvatar);
    }

    // 绑定修改密码模态框按钮事件
    const changePasswordBtn = document.querySelector('button[onclick="showChangePasswordModal()"]');
    if (changePasswordBtn) {
        changePasswordBtn.addEventListener('click', function() {
            document.getElementById('change-password-modal').classList.remove('hidden');
        });
    }

    // 确认修改密码按钮
    const submitPasswordBtn = document.querySelector('button[onclick="submitChangePassword()"]');
    if (submitPasswordBtn) {
        submitPasswordBtn.addEventListener('click', submitChangePassword);
    }

    // 取消修改密码按钮
    const cancelPasswordBtn = document.querySelector('button[onclick="hideChangePasswordModal()"]');
    if (cancelPasswordBtn) {
        cancelPasswordBtn.addEventListener('click', function() {
            document.getElementById('change-password-modal').classList.add('hidden');
        });
    }
});

function inviteUserAsAdmin() {
    const kbId = currentKnowledgeBaseId; // 使用全局变量获取当前知识库ID
    const email = document.getElementById('invite-email').value;

    if (!email || !email.trim()) {
        UIController.showToast('请输入邀请邮箱', 'error');
        return;
    }

    if (!kbId) {
        UIController.showToast('未找到当前知识库', 'error');
        return;
    }

    // 显示加载状态
    UIController.showLoading();

    // 发送邀请请求
    fetch(`/Cloud/knowledge/member/invite`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            knowledgeBaseId: kbId,
            email: email,
            role: 'ADMIN' // 指定为管理员角色
        })
    })
        .then(response => response.json())
        .then(data => {
            if (data.code === 200) {
                UIController.showToast('邀请成功', 'success');
                // 清空输入框
                document.getElementById('invite-email').value = '';
                // 刷新成员列表
                loadMembers(kbId);
            } else {
                UIController.showToast(data.message || '邀请失败', 'error');
            }
        })
        .catch(error => {
            UIController.showToast('邀请失败: ' + error.message, 'error');
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// function uploadImage() {
//     const input = document.createElement('input');
//     input.type = 'file';
//     input.accept = 'image/*';
//     input.onchange = function () {
//         const file = this.files[0];
//         const formData = new FormData();
//         formData.append('image', file);
//         const xhr = new XMLHttpRequest();
//         xhr.open('POST', '/Cloud/upload-image', true);
//         xhr.onreadystatechange = function () {
//             if (xhr.readyState === 4 && xhr.status === 200) {
//                 const response = JSON.parse(xhr.responseText);
//                 if (response.code === 200) {
//                     const imgUrl = response.data.url;
//                     const img = document.createElement('img');
//                     img.src = imgUrl;
//                     const editor = document.getElementById('editor-content');
//                     editor.appendChild(img);
//                 } else {
//                     alert('图片上传失败：' + response.message);
//                 }
//             }
//         };
//         xhr.send(formData);
//     };
//     input.click();
// }

function toggleFollow(userId) {
    const followButton = document.querySelector(`#user-profile-${userId} .follow-button`);
    const isFollowed = followButton.getAttribute('data-followed') === 'true';

    if (isFollowed) {
        // 取消关注
        followButton.textContent = '关注';
        followButton.setAttribute('data-followed', 'false');
    } else {
        // 关注
        followButton.textContent = '已关注';
        followButton.setAttribute('data-followed', 'true');
    }

    // 发送 AJAX 请求更新关注状态
    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/Cloud/toggle-follow', true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            const response = JSON.parse(xhr.responseText);
            if (response.code === 200) {
                // 更新成功
            }
        }
    };
    const data = JSON.stringify({ userId, isFollowed: !isFollowed });
    xhr.send(data);
}

function getFollowList() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', '/Cloud/follow-list', true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            const response = JSON.parse(xhr.responseText);
            if (response.code === 200) {
                const followList = response.data;
                const followListDiv = document.getElementById('follow-list');
                followListDiv.innerHTML = '';
                followList.forEach(user => {
                    const userDiv = document.createElement('div');
                    userDiv.innerHTML = `
                        <p><a href="#" onclick="showUserProfile(${user.id})">${user.username}</a></p>
                    `;
                    followListDiv.appendChild(userDiv);
                });
            }
        }
    };
    xhr.send();
}

window.onload = function() {
    loadBrowseHistory();
};

// 当前知识库ID
let currentKnowledgeBaseId = null;

// 初始化知识库首页
function initKnowledgePage() {
    // 加载我的知识库列表
    loadMyKnowledgeBases();

    // 加载最近访问的知识库
    loadRecentKnowledgeBases();

    // 加载推荐公开知识库
    loadRecommendedKnowledgeBases();

    // 加载最近发表的文章
    loadRecentArticles();

    // 加载我的知识库列表（用于下拉选择）
    loadMyKnowledgeBases();

    // 初始化消息通知
    initNotifications();

    // 加载用户信息
    loadUserInfo();
}

// 加载我的知识库列表
function loadMyKnowledgeBases() {
    UIController.showLoading();

    ApiService.knowledgeBase.getMyList()
        .then(res => {
            if (res.code === 200 && res.data) {
                // 获取我的知识库容器
                const container = document.getElementById('my-knowledgebases');
                container.innerHTML = '';

                // 如果没有知识库，显示提示
                if (res.data.length === 0) {
                    container.innerHTML = '<div class="text-center text-gray-500 p-4">你还没有创建任何知识库，点击下方按钮创建第一个知识库！</div>';
                    return;
                }

                // 渲染知识库卡片
                res.data.forEach(kb => {
                    container.appendChild(createKnowledgeBaseCard(kb));
                });
            } else {
                UIController.showToast(res.message || '加载我的知识库失败', 'error');
            }
        })
        .catch(err => {
            UIController.showToast(err.message || '加载我的知识库失败', 'error');
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// 加载最近访问的知识库
function loadRecentKnowledgeBases() {
    UIController.showLoading();

    ApiService.knowledgeBase.getMyList()
        .then(res => {
            if (res.code === 200 && res.data) {
                // 获取最近访问的知识库容器
                const container = document.getElementById('recent-knowledgebases');
                container.innerHTML = '';

                // 如果没有知识库，显示提示
                if (res.data.length === 0) {
                    container.innerHTML = '<div class="text-center text-gray-500 p-4">暂无最近访问的知识库</div>';
                    return;
                }

                // 最多显示6个最近访问的知识库
                const recentKnowledgeBases = res.data.slice(0, 6);

                // 渲染知识库卡片
                recentKnowledgeBases.forEach(kb => {
                    container.appendChild(createKnowledgeBaseCard(kb));
                });
            } else {
                UIController.showToast(res.message || '加载最近访问的知识库失败', 'error');
            }
        })
        .catch(err => {
            UIController.showToast(err.message || '加载最近访问的知识库失败', 'error');
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// 加载推荐公开知识库
function loadRecommendedKnowledgeBases() {
    UIController.showLoading();

    ApiService.knowledgeBase.getPublicList()
        .then(res => {
            if (res.code === 200 && res.data) {
                // 获取推荐知识库容器
                const container = document.getElementById('recommended-knowledgebases');
                container.innerHTML = '';

                // 如果没有知识库，显示提示
                if (res.data.length === 0) {
                    container.innerHTML = '<div class="text-center text-gray-500 p-4">暂无推荐公开知识库</div>';
                    return;
                }

                // 最多显示6个推荐知识库
                const recommendedKnowledgeBases = res.data.slice(0, 6);

                // 渲染知识库卡片
                recommendedKnowledgeBases.forEach(kb => {
                    container.appendChild(createKnowledgeBaseCard(kb));
                });
            } else {
                UIController.showToast(res.message || '加载推荐公开知识库失败', 'error');
            }
        })
        .catch(err => {
            UIController.showToast(err.message || '加载推荐公开知识库失败', 'error');
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// // 加载我的知识库列表（用于下拉选择）
// function loadMyKnowledgeBases() {
//     ApiService.knowledgeBase.getMyList()
//         .then(res => {
//             if (res.code === 200 && res.data) {
//                 // 获取下拉选择框
//                 const select = document.getElementById('kb-select');
//                 select.innerHTML = '<option value="">选择知识库</option>';
//
//                 // 添加知识库选项
//                 res.data.forEach(kb => {
//                     const option = document.createElement('option');
//                     option.value = kb.id;
//                     option.textContent = kb.name;
//                     select.appendChild(option);
//                 });
//
//                 // 绑定选择事件
//                 select.addEventListener('change', function() {
//                     if (this.value) {
//                         showKnowledgeDetail(this.value);
//                     }
//                 });
//             }
//         })
//         .catch(err => {
//             console.error('加载我的知识库列表失败:', err);
//         });
// }

// 显示消息列表函数
function showMessages() {
    // 这里可以实现显示消息列表的逻辑
    if (UIController && typeof UIController.showToast === 'function') {
        UIController.showToast('消息功能正在开发中', 'info');
    } else {
        alert('消息功能正在开发中');
    }

    // 清除消息计数
    const badges = document.querySelectorAll('.message-badge');
    badges.forEach(badge => {
        badge.textContent = '0';
        badge.classList.add('hidden');
    });
}

// 获取当前文章ID
function getCurrentArticleId() {
    // 假设文章 ID 存储在 id 为 'article' 的元素的 data-id 属性中
    const likeButton = document.getElementById('article-like-button');
    const articleId = likeButton.getAttribute('data-id');
    if (!articleId) {
        console.error('未找到文章ID');
        return null;
    }
    return articleId;
}

// 删除知识库
function deleteKnowledgeBase() {
    if (!currentKnowledgeBaseId) {
        UIController.showToast('未选择知识库', 'error');
        return;
    }

    // 确认删除
    UIController.showConfirm('确定要删除此知识库吗？此操作不可撤销！', () => {
        // 显示加载状态
        UIController.showLoading();

        // 调用删除知识库API
        ApiService.knowledgeBase.delete(currentKnowledgeBaseId)
            .then(res => {
                if (res.code === 200) {
                    UIController.showToast('删除知识库成功', 'success');

                    // 返回首页
                    UIController.showPage('homePage');

                    // 重新加载知识库列表
                    loadRecentKnowledgeBases();
                    loadMyKnowledgeBases();

                    // 清除当前知识库ID
                    currentKnowledgeBaseId = null;
                } else {
                    UIController.showToast(res.message || '删除知识库失败', 'error');
                }
            })
            .catch(err => {
                UIController.showToast(err.message || '删除知识库失败', 'error');
            })
            .finally(() => {
                UIController.hideLoading();
            });
    });
}

// 更新知识库
function updateKnowledgeBase() {
    if (!currentKnowledgeBaseId) {
        UIController.showToast('未选择知识库', 'error');
        return;
    }

    // 获取表单数据
    const name = document.getElementById('edit-kb-name').value;
    const description = document.getElementById('edit-kb-description').value;
    const isPublic = document.getElementById('edit-kb-access').value === 'public';

    // 表单验证
    if (!name) {
        UIController.showToast('请输入知识库名称', 'error');
        return;
    }

    // 显示加载状态
    UIController.showLoading();

    // 调用更新知识库API
    ApiService.knowledgeBase.update(currentKnowledgeBaseId, name, description, isPublic)
        .then(res => {
            if (res.code === 200) {
                UIController.showToast('更新知识库成功', 'success');

                // 关闭模态框
                hideEditKnowledgeBaseModal();

                // 重新加载知识库详情
                showKnowledgeDetail(currentKnowledgeBaseId);
            } else {
                UIController.showToast(res.message || '更新知识库失败', 'error');
            }
        })
        .catch(err => {
            UIController.showToast(err.message || '更新知识库失败', 'error');
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// 隐藏编辑知识库模态框
function hideEditKnowledgeBaseModal() {
    document.getElementById('edit-knowledgebase-modal').classList.add('hidden');
}

// 显示创建知识库模态框
function showCreateKnowledgeBaseModal() {
    document.getElementById('create-knowledgebase-modal').classList.remove('hidden');

    // 重置表单
    document.getElementById('kb-name').value = '';
    document.getElementById('kb-description').value = '';
    document.getElementById('kb-access').value = 'private';
}

// 隐藏创建知识库模态框
function hideCreateKnowledgeBaseModal() {
    document.getElementById('create-knowledgebase-modal').classList.add('hidden');
}

// 显示编辑知识库模态框
function showEditKnowledgeBaseModal() {
    if (!currentKnowledgeBaseId) {
        UIController.showToast('未选择知识库', 'error');
        return;
    }

    // 获取当前知识库信息
    UIController.showLoading();

    ApiService.knowledgeBase.getDetail(currentKnowledgeBaseId)
        .then(res => {
            if (res.code === 200 && res.data) {
                // 显示模态框
                document.getElementById('edit-knowledgebase-modal').classList.remove('hidden');

                // 填充表单
                document.getElementById('edit-kb-name').value = res.data.name;
                document.getElementById('edit-kb-description').value = res.data.description || '';
                document.getElementById('edit-kb-access').value = res.data.isPublic ? 'public' : 'private';
            } else {
                UIController.showToast(res.message || '获取知识库信息失败', 'error');
            }
        })
        .catch(err => {
            UIController.showToast(err.message || '获取知识库信息失败', 'error');
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// 显示知识库详情
function showKnowledgeDetail(id) {
    // 设置当前知识库 ID
    currentKnowledgeBaseId = id;

    // 显示知识库详情页
    UIController.showPage('knowledge-detail');
    // 加载知识库详情
    UIController.showLoading();

    // 如果在成员管理页面，加载成员列表
    if (document.getElementById('member-management') &&
        !document.getElementById('member-management').classList.contains('hidden')) {
        loadMembers(id);
    }

    ApiService.knowledgeBase.getDetail(id)
        .then(res => {
            if (res.code === 200 && res.data) {
                console.log(res.data);
                // 显示知识库信息
                document.getElementById('kb-name-display').textContent = res.data.name;
                console.log(res.data.name)
                document.getElementById('kb-description-display').textContent = res.data.description || '暂无描述';
                document.getElementById('kb-creator-display').textContent = res.data.creatorName;
                document.getElementById('kb-create-time-display').textContent = new Date(res.data.createTime).toLocaleString();
                document.getElementById('kb-access-display').textContent = res.data.isPublic ? '公开' : '私有';
                //
                // // 加载文档列表
                // loadDocumentList(id);

                // 加载文章列表
                loadArticleList(id);
            } else {
                UIController.showToast(res.message || '加载知识库详情失败', 'error');
            }
        })
        .catch(err => {
            UIController.showToast(err.message || '加载知识库详情失败', 'error');
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// 创建知识库卡片
function createKnowledgeBaseCard(kb) {
    const card = document.createElement('div');
    card.className = 'knowledge-card bg-white shadow-md rounded p-4';
    card.onclick = () => showKnowledgeDetail(kb.id);

    // Generate random cover image if not provided
    const coverUrl = kb.coverUrl || `https://picsum.photos/seed/${kb.id}/200/150`;

    // Set card content
    card.innerHTML = `
    <img src="${coverUrl}" alt="知识库封面" class="rounded">
    <h3 class="text-lg font-bold mt-2">${kb.name}</h3>
    <p class="text-gray-600">作者：${kb.creatorName || '未知'} | 成员数：${kb.memberCount || 0}</p>
    <span class="badge ${kb.isPublic ? 'public bg-green-500' : 'private bg-gray-500'} text-white px-2 py-1 rounded mt-2 inline-block">${kb.isPublic ? '公开' : '私有'}</span>
    <i class="favorite ${kb.isFavorite ? 'fas' : 'far'} fa-heart" onclick="toggleFavorite(event, ${kb.id})"></i>
  `;

    return card;
}

// 初始化通知功能
function initNotifications() {
    // 绑定通知图标点击事件
    document.querySelectorAll('.fa-bell').forEach(icon => {
        icon.addEventListener('click', showMessages);
    });

    // 模拟有新消息 (在实际项目中这部分应该通过API获取)
    setTimeout(() => {
        const badges = document.querySelectorAll('.message-badge');
        badges.forEach(badge => {
            badge.textContent = '3';
            badge.classList.remove('hidden');
        });
    }, 3000);
}

// 生成分页按钮
function generatePagination(total, current, size, containerId, callback) {
    const container = document.getElementById(containerId);
    if (!container) return;

    // Clear container
    container.innerHTML = '';

    // Calculate total pages
    const totalPages = Math.ceil(total / size) || 1;

    // If only one page, don't show pagination
    if (totalPages <= 1) return;

    // Create pagination container
    const pagination = document.createElement('div');
    pagination.className = 'flex justify-center items-center space-x-2 mt-4';

    // Add previous page button
    const prevButton = document.createElement('button');
    prevButton.className = `px-3 py-1 rounded border ${current <= 1 ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'bg-white text-gray-700 hover:bg-gray-50'}`;
    prevButton.textContent = '上一页';
    prevButton.disabled = current <= 1;

    if (current > 1) {
        prevButton.onclick = () => callback(current - 1);
    }

    pagination.appendChild(prevButton);

    // Determine page range to display
    let startPage = Math.max(1, current - 2);
    let endPage = Math.min(totalPages, startPage + 4);

    if (endPage - startPage < 4) {
        startPage = Math.max(1, endPage - 4);
    }

    // Add first page and ellipsis if needed
    if (startPage > 1) {
        const firstPageButton = document.createElement('button');
        firstPageButton.className = 'px-3 py-1 rounded border bg-white text-gray-700 hover:bg-gray-50';
        firstPageButton.textContent = '1';
        firstPageButton.onclick = () => callback(1);
        pagination.appendChild(firstPageButton);

        if (startPage > 2) {
            const ellipsis = document.createElement('span');
            ellipsis.className = 'px-2 py-1';
            ellipsis.textContent = '...';
            pagination.appendChild(ellipsis);
        }
    }

    // Add page buttons
    for (let i = startPage; i <= endPage; i++) {
        const pageButton = document.createElement('button');

        // Style current page differently
        if (i === current) {
            pageButton.className = 'px-3 py-1 rounded border bg-blue-500 text-white';
        } else {
            pageButton.className = 'px-3 py-1 rounded border bg-white text-gray-700 hover:bg-gray-50';
        }

        pageButton.textContent = i;
        pageButton.onclick = () => callback(i);
        pagination.appendChild(pageButton);
    }

    // Add last page and ellipsis if needed
    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            const ellipsis = document.createElement('span');
            ellipsis.className = 'px-2 py-1';
            ellipsis.textContent = '...';
            pagination.appendChild(ellipsis);
        }

        const lastPageButton = document.createElement('button');
        lastPageButton.className = 'px-3 py-1 rounded border bg-white text-gray-700 hover:bg-gray-50';
        lastPageButton.textContent = totalPages;
        lastPageButton.onclick = () => callback(totalPages);
        pagination.appendChild(lastPageButton);
    }

    // Add next page button
    const nextButton = document.createElement('button');
    nextButton.className = `px-3 py-1 rounded border ${current >= totalPages ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'bg-white text-gray-700 hover:bg-gray-50'}`;
    nextButton.textContent = '下一页';
    nextButton.disabled = current >= totalPages;

    if (current < totalPages) {
        nextButton.onclick = () => callback(current + 1);
    }

    pagination.appendChild(nextButton);

    // Add pagination to container
    container.appendChild(pagination);
}

function createPagination(currentPage, totalPages, onPageChange, containerId, sortBy = null) {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.innerHTML = '';

    if (totalPages <= 1) return;

    const pagination = document.createElement('nav');
    pagination.className = 'inline-flex rounded-md shadow';

    // Previous page button
    const prevBtn = document.createElement('a');
    prevBtn.href = "javascript:void(0)";
    prevBtn.className = `px-3 py-1 rounded-l-md border border-gray-300 ${currentPage === 1 ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'bg-white text-gray-500 hover:bg-gray-50'}`;
    prevBtn.textContent = '上一页';

    if (currentPage > 1) {
        prevBtn.addEventListener('click', () => {
            if (sortBy) {
                onPageChange(currentPage - 1, sortBy);
            } else {
                onPageChange(currentPage - 1);
            }
        });
    }

    pagination.appendChild(prevBtn);

    // Page number buttons
    const pageCount = Math.min(totalPages, 5); // Show at most 5 page numbers
    let startPage = Math.max(1, currentPage - 2);
    let endPage = Math.min(startPage + pageCount - 1, totalPages);

    if (endPage - startPage + 1 < pageCount) {
        startPage = Math.max(1, endPage - pageCount + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
        const pageBtn = document.createElement('a');
        pageBtn.href = "javascript:void(0)";
        pageBtn.className = `px-3 py-1 border-t border-b border-gray-300 ${i === currentPage ? 'bg-blue-500 text-white' : 'bg-white text-gray-500 hover:bg-gray-50'}`;
        pageBtn.textContent = i.toString();

        if (i !== currentPage) {
            pageBtn.addEventListener('click', () => {
                if (sortBy) {
                    onPageChange(i, sortBy);
                } else {
                    onPageChange(i);
                }
            });
        }

        pagination.appendChild(pageBtn);
    }

    // Next page button
    const nextBtn = document.createElement('a');
    nextBtn.href = "javascript:void(0)";
    nextBtn.className = `px-3 py-1 rounded-r-md border border-gray-300 ${currentPage === totalPages ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'bg-white text-gray-500 hover:bg-gray-50'}`;
    nextBtn.textContent = '下一页';

    if (currentPage < totalPages) {
        nextBtn.addEventListener('click', () => {
            if (sortBy) {
                onPageChange(currentPage + 1, sortBy);
            } else {
                onPageChange(currentPage + 1);
            }
        });
    }

    pagination.appendChild(nextBtn);

    container.appendChild(pagination);
}