// 加载成员列表
// function loadMembers(knowledgeId) {
//     $.get(`/api/knowledge/member/list/${knowledgeId}`, function(res) {
//         if (res.code === 200) {
//             renderMemberList(res.data);
//         } else {
//             alert(res.message);
//         }
//     }).fail(function() {
//         alert('加载成员列表失败');
//     });
// }

// 渲染成员列表
// function renderMemberList(members) {
//     const $container = $('#memberList');
//     $container.empty();
//
//     members.forEach(member => {
//         const roleName = getRoleName(member.role);
//         const row = `
//         <tr>
//             <td>
//                 <div class="d-flex align-items-center">
//                     <img src="${member.avatar || '/static/img/default-avatar.png'}"
//                          class="avatar-sm rounded-circle mr-2">
//                     <span>${member.username}</span>
//                 </div>
//             </td>
//             <td>${roleName}</td>
//             <td>${new Date(member.joinTime).toLocaleString()}</td>
//             <td>
//                 ${member.role > 1 ? `<button class="btn btn-sm btn-outline-danger"
//                     onclick="removeMember(${member.knowledgeId}, ${member.userId})">
//                     移除
//                 </button>` : ''}
//                 ${member.role === 3 ? `<button class="btn btn-sm btn-outline-primary ml-2"
//                     onclick="promoteToAdmin(${member.knowledgeId}, ${member.userId})">
//                     设为管理员
//                 </button>` : ''}
//             </td>
//         </tr>`;
//         $container.append(row);
//     });
// }

// 添加成员
function addMember() {
    const data = {
        knowledgeId: $('#knowledgeId').val(),
        userId: $('#userIdentifier').val(), // 实际应该先搜索用户获取ID
        role: $('#memberRole').val()
    };

    $.ajax({
        url: '/knowledge/member',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function(res) {
            if (res.code === 200) {
                $('#addMemberModal').modal('hide');
                loadMembers(data.knowledgeId);
            } else {
                alert(res.message);
            }
        },
        error: function(xhr) {
            alert('操作失败: ' + (xhr.responseJSON?.message || '服务器错误'));
        }
    });
}

// 辅助函数：获取角色名称
function getRoleName(roleCode) {
    switch(roleCode) {
        case 1: return '所有者';
        case 2: return '管理员';
        default: return '成员';
    }
}

/**
 * 成员管理脚本
 * 处理知识库成员邀请、权限管理等功能
 */

// 当前知识库ID
let currentMemberKnowledgeBaseId = null;

// 初始化成员管理页面
function initMemberManagementPage(knowledgeBaseId) {
    // 设置当前知识库ID
    currentMemberKnowledgeBaseId = knowledgeBaseId;

    // 加载成员列表
    loadMembers(knowledgeBaseId);

    // 绑定搜索框事件
    document.getElementById('member-search').addEventListener('input', debounce(searchMembers, 500));

    // 绑定角色筛选事件
    document.getElementById('role-filter').addEventListener('change', filterMembersByRole);

    // 绑定邀请按钮事件
    document.getElementById('invite-btn').addEventListener('click', () => {
        document.getElementById('invite-modal').classList.remove('hidden');
    });

    // 绑定取消邀请按钮事件
    document.getElementById('cancel-invite-btn').addEventListener('click', () => {
        document.getElementById('invite-modal').classList.add('hidden');
    });

    // 绑定提交邀请按钮事件
    document.getElementById('submit-invite-btn').addEventListener('click', inviteUser);
}

// 加载成员列表
function loadMembers(knowledgeBaseId) {
    // 显示加载状态
    UIController.showLoading();

    ApiService.knowledgeMember.getList(knowledgeBaseId)
        .then(res => {
            if (res.code === 200 && res.data) {
                // 渲染成员列表
                renderMemberList(res.data);
            } else {
                UIController.showToast(res.message || '加载成员列表失败', 'error');
            }
        })
        .catch(err => {
            UIController.showToast(err.message || '加载成员列表失败', 'error');
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// 渲染成员列表
function renderMemberList(members) {
    // 获取成员列表容器
    const tableBody = document.getElementById('member-list');
    tableBody.innerHTML = '';

    // 如果没有成员，显示提示
    if (members.length === 0) {
        const emptyRow = document.createElement('tr');
        emptyRow.innerHTML = '<td colspan="5" class="border border-gray-300 p-4 text-center">暂无成员</td>';
        tableBody.appendChild(emptyRow);
        return;
    }

    // 遍历成员列表
    members.forEach(member => {
        const row = document.createElement('tr');

        // 格式化加入时间
        const joinTime = new Date(member.joinTime).toLocaleString();

        // 角色中文名称
        const roleName = member.role === 1 ? '所有者' :
            member.role === 2 ? '管理员' : '成员';

        // 获取当前用户角色
        const currentUserRole = getCurrentUserRole(members);

        // 设置行内容
        row.innerHTML = `
      <td class="border border-gray-300 p-2">
        <img src="${member.avatar || 'https://picsum.photos/30/30'}" alt="用户头像" class="rounded-full w-8 h-8">
      </td>
      <td class="border border-gray-300 p-2">${member.username}</td>
      <td class="border border-gray-300 p-2">
        ${currentUserRole <= member.role ? roleName :
            `<select class="border border-gray-300 p-1 rounded role-select" data-user-id="${member.userId}" data-original-role="${member.role}">
            <option value="2" ${member.role === 2 ? 'selected' : ''}>管理员</option>
            <option value="3" ${member.role === 3 ? 'selected' : ''}>成员</option>
          </select>`
        }
      </td>
      <td class="border border-gray-300 p-2">${joinTime}</td>
      <td class="border border-gray-300 p-2">
        ${member.role === 1 ? '无法操作' :
            currentUserRole <= member.role ?
                `<button class="text-red-500" onclick="removeMember(${member.userId})">
            <i class="fas fa-user-minus"></i> 移除
          </button>` : '权限不足'
        }
      </td>
    `;

        tableBody.appendChild(row);
    });

    // 绑定角色选择事件
    document.querySelectorAll('.role-select').forEach(select => {
        select.addEventListener('change', function() {
            const userId = this.dataset.userId;
            const originalRole = this.dataset.originalRole;
            const newRole = this.value;

            // 如果角色没有变化，不做处理
            if (originalRole === newRole) return;

            // 确认修改
            UIController.showConfirm('确定要修改该成员的角色吗？', () => {
                updateMemberRole(userId, newRole);
            }, () => {
                // 取消修改，恢复原来的选择
                this.value = originalRole;
            });
        });
    });
}

// 获取当前用户角色
function getCurrentUserRole(members) {
    // 获取当前用户ID
    const currentUser = JSON.parse(localStorage.getItem('currentUser') || '{}');

    if (!currentUser.id) return 3; // 默认为普通成员权限

    // 查找当前用户在成员列表中的角色
    const currentMember = members.find(member => member.userId === currentUser.id);

    return currentMember ? currentMember.role : 3;
}

// 搜索成员
function searchMembers() {
    const keyword = document.getElementById('member-search').value.trim().toLowerCase();

    // 如果关键词为空，加载全部成员
    if (!keyword) {
        loadMembers(currentMemberKnowledgeBaseId);
        return;
    }

    // 获取成员列表
    const rows = document.querySelectorAll('#member-list tr');

    // 遍历成员行
    rows.forEach(row => {
        const name = row.cells[1].textContent.toLowerCase();

        // 如果名称包含关键词，显示此行，否则隐藏
        if (name.includes(keyword)) {
            row.classList.remove('hidden');
        } else {
            row.classList.add('hidden');
        }
    });
}

// 按角色筛选成员
function filterMembersByRole() {
    const roleValue = document.getElementById('role-filter').value;

    // 如果选择全部角色，显示所有成员
    if (roleValue === 'all') {
        document.querySelectorAll('#member-list tr').forEach(row => {
            row.classList.remove('hidden');
        });
        return;
    }

    // 获取成员列表
    const rows = document.querySelectorAll('#member-list tr');

    // 遍历成员行
    rows.forEach(row => {
        const roleCell = row.cells[2];
        let rowRole;

        // 获取角色值
        if (roleCell.querySelector('select')) {
            rowRole = roleCell.querySelector('select').value;
        } else {
            // 从文本内容判断角色
            const roleName = roleCell.textContent.trim();
            rowRole = roleName === '所有者' ? '1' :
                roleName === '管理员' ? '2' : '3';
        }

        // 如果角色匹配，显示此行，否则隐藏
        if (rowRole === roleValue) {
            row.classList.remove('hidden');
        } else {
            row.classList.add('hidden');
        }
    });
}

// 邀请用户
function inviteUser() {
    // 获取邀请表单数据
    const identifier = document.getElementById('invite-identifier').value;
    const role = document.getElementById('invite-role').value;

    // 表单验证
    if (!identifier) {
        UIController.showToast('请输入用户名或邮箱', 'error');
        return;
    }

    // 显示加载状态
    UIController.showLoading();

    // 调用邀请API
    ApiService.knowledgeMember.addByIdentifier(currentMemberKnowledgeBaseId, identifier, role)
        .then(res => {
            if (res.code === 200) {
                UIController.showToast('邀请成功', 'success');

                // 关闭模态框
                document.getElementById('invite-modal').classList.add('hidden');

                // 清空表单
                document.getElementById('invite-identifier').value = '';

                // 重新加载成员列表
                loadMembers(currentMemberKnowledgeBaseId);
            } else {
                UIController.showToast(res.message || '邀请失败', 'error');
            }
        })
        .catch(err => {
            UIController.showToast(err.message || '邀请失败', 'error');
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// 移除成员
function removeMember(userId) {
    // 确认移除
    UIController.showConfirm('确定要移除该成员吗？', () => {
        // 显示加载状态
        UIController.showLoading();

        // 调用移除成员API
        ApiService.knowledgeMember.remove(currentMemberKnowledgeBaseId, userId)
            .then(res => {
                if (res.code === 200) {
                    UIController.showToast('移除成员成功', 'success');

                    // 重新加载成员列表
                    loadMembers(currentMemberKnowledgeBaseId);
                } else {
                    UIController.showToast(res.message || '移除成员失败', 'error');
                }
            })
            .catch(err => {
                UIController.showToast(err.message || '移除成员失败', 'error');
            })
            .finally(() => {
                UIController.hideLoading();
            });
    });
}

// 更新成员角色
function updateMemberRole(userId, role) {
    // 显示加载状态
    UIController.showLoading();

    // 调用更新角色API
    ApiService.knowledgeMember.updateRole(currentMemberKnowledgeBaseId, userId, role)
        .then(res => {
            if (res.code === 200) {
                UIController.showToast('更新角色成功', 'success');

                // 重新加载成员列表
                loadMembers(currentMemberKnowledgeBaseId);
            } else {
                UIController.showToast(res.message || '更新角色失败', 'error');
            }
        })
        .catch(err => {
            UIController.showToast(err.message || '更新角色失败', 'error');
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// 批量操作
function batchOperation() {
    const operation = document.getElementById('batch-operation').value;

    if (!operation) {
        UIController.showToast('请选择操作', 'error');
        return;
    }

    // 获取选中的成员
    const checkedMembers = document.querySelectorAll('.member-checkbox:checked');

    if (checkedMembers.length === 0) {
        UIController.showToast('请选择成员', 'error');
        return;
    }

    // 提取成员ID
    const memberIds = Array.from(checkedMembers).map(checkbox => checkbox.dataset.userId);

    // 根据操作类型执行不同的操作
    if (operation === 'remove') {
        // 批量移除成员
        UIController.showConfirm(`确定要移除所选的 ${memberIds.length} 名成员吗？`, () => {
            batchRemoveMembers(memberIds);
        });
    } else if (operation === 'role') {
        // 批量修改角色
        document.getElementById('batch-role-modal').classList.remove('hidden');
    }
}

// 批量移除成员
function batchRemoveMembers(memberIds) {
    // 显示加载状态
    UIController.showLoading();

    // 使用Promise.all并行处理多个请求
    Promise.all(memberIds.map(userId =>
        ApiService.knowledgeMember.remove(currentMemberKnowledgeBaseId, userId)
    ))
        .then(results => {
            // 检查是否所有请求都成功
            const allSuccess = results.every(res => res.code === 200);

            if (allSuccess) {
                UIController.showToast('批量移除成员成功', 'success');
            } else {
                UIController.showToast('部分成员移除失败', 'warning');
            }

            // 重新加载成员列表
            loadMembers(currentMemberKnowledgeBaseId);
        })
        .catch(err => {
            UIController.showToast('批量操作失败', 'error');
            console.error('批量移除成员失败:', err);
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// 批量修改角色
function batchUpdateRole() {
    // 获取选中的成员
    const checkedMembers = document.querySelectorAll('.member-checkbox:checked');

    if (checkedMembers.length === 0) {
        UIController.showToast('请选择成员', 'error');
        return;
    }

    // 提取成员ID
    const memberIds = Array.from(checkedMembers).map(checkbox => checkbox.dataset.userId);

    // 获取目标角色
    const targetRole = document.getElementById('batch-target-role').value;

    // 显示加载状态
    UIController.showLoading();

    // 使用Promise.all并行处理多个请求
    Promise.all(memberIds.map(userId =>
        ApiService.knowledgeMember.updateRole(currentMemberKnowledgeBaseId, userId, targetRole)
    ))
        .then(results => {
            // 检查是否所有请求都成功
            const allSuccess = results.every(res => res.code === 200);

            if (allSuccess) {
                UIController.showToast('批量修改角色成功', 'success');
            } else {
                UIController.showToast('部分成员角色修改失败', 'warning');
            }

            // 关闭模态框
            document.getElementById('batch-role-modal').classList.add('hidden');

            // 重新加载成员列表
            loadMembers(currentMemberKnowledgeBaseId);
        })
        .catch(err => {
            UIController.showToast('批量操作失败', 'error');
            console.error('批量修改角色失败:', err);
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// 全选/取消全选
function toggleSelectAll() {
    const selectAllCheckbox = document.getElementById('select-all');
    const memberCheckboxes = document.querySelectorAll('.member-checkbox');

    memberCheckboxes.forEach(checkbox => {
        checkbox.checked = selectAllCheckbox.checked;
    });
}

// 防抖函数
function debounce(func, delay) {
    let timer;
    return function() {
        const context = this;
        const args = arguments;
        clearTimeout(timer);
        timer = setTimeout(() => {
            func.apply(context, args);
        }, delay);
    };
}

// 导出模块
window.MemberManager = {
    initMemberManagementPage,
    loadMembers,
    searchMembers,
    filterMembersByRole,
    inviteUser,
    removeMember,
    updateMemberRole,
    batchOperation,
    batchRemoveMembers,
    batchUpdateRole,
    toggleSelectAll
};