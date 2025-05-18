// 修复浏览记录相关的函数

// 加载浏览记录
function loadBrowseHistory(page = 1, size = 10) {
    // 显示加载状态
    if (UIController && typeof UIController.showLoading === 'function') {
        UIController.showLoading();
    }

    // 获取容器
    const container = document.getElementById('browse-history');
    if (!container) {
        if (UIController && typeof UIController.hideLoading === 'function') {
            UIController.hideLoading();
        }
        return;
    }

    // 清空容器，添加加载提示
    container.innerHTML = '<div class="text-center text-gray-500 p-4">加载中...</div>';

    // 调用API获取浏览记录
    if (ApiService && ApiService.browseHistory && typeof ApiService.browseHistory.getList === 'function') {
        ApiService.browseHistory.getList(page, size)
            .then(res => {
                // 清空容器
                container.innerHTML = '';

                if (res.code === 200) {
                    // 检查数据格式
                    let histories = [];
                    let total = 0;

                    // 兼容不同的后端返回格式
                    if (Array.isArray(res.data)) {
                        histories = res.data;
                        total = res.total || histories.length;
                    } else if (res.data && res.data.list) {
                        histories = res.data.list;
                        total = res.data.total;
                    } else if (res.data) {
                        histories = res.data;
                        total = res.total || histories.length;
                    }

                    // 如果没有浏览记录，显示提示
                    if (histories.length === 0) {
                        container.innerHTML = '<div class="text-center text-gray-500 p-4">暂无浏览记录</div>';
                        return;
                    }

                    // 渲染浏览记录
                    histories.forEach(history => {
                        const item = document.createElement('div');
                        item.className = 'browse-history-item p-2 hover:bg-gray-100 rounded flex justify-between';

                        // 格式化时间
                        const browseTime = new Date(history.browseTime).toLocaleString();

                        // 设置列表项内容
                        item.innerHTML = `
                            <div class="flex-1 cursor-pointer" onclick="showArticleDetail(${history.articleId})">
                                <h4 class="text-md font-bold">${history.articleTitle || '未知文章'}</h4>
                                <p class="text-gray-600 text-sm">浏览时间：${browseTime}</p>
                            </div>
                            <button class="text-red-500" onclick="deleteBrowseHistory(${history.id}, event)">
                                <i class="fas fa-trash"></i>
                            </button>
                        `;

                        container.appendChild(item);
                    });

                    // 计算总页数
                    const totalPages = Math.ceil(total / size) || 1;

                    // 如果总页数大于1，添加分页控件
                    if (totalPages > 1) {
                        // 创建分页容器
                        const paginationContainer = document.createElement('div');
                        paginationContainer.id = 'browse-history-pagination';
                        paginationContainer.className = 'mt-4';
                        container.appendChild(paginationContainer);

                        // 创建分页
                        createPagination(page, totalPages,
                            newPage => loadBrowseHistory(newPage, size),
                            'browse-history-pagination');
                    }

                    // 添加清空按钮
                    if (histories.length > 0) {
                        const clearButton = document.createElement('button');
                        clearButton.className = 'bg-red-500 text-white px-4 py-2 rounded mt-4';
                        clearButton.textContent = '清空浏览记录';
                        clearButton.onclick = clearBrowseHistory;
                        container.appendChild(clearButton);
                    }
                } else {
                    container.innerHTML = `<div class="text-center text-red-500 p-4">加载失败: ${res.message || '未知错误'}</div>`;
                    if (UIController && typeof UIController.showToast === 'function') {
                        UIController.showToast(res.message || '加载浏览记录失败', 'error');
                    }
                }
            })
            .catch(err => {
                container.innerHTML = '<div class="text-center text-red-500 p-4">加载失败，请稍后重试</div>';
                if (UIController && typeof UIController.showToast === 'function') {
                    UIController.showToast(err.message || '加载浏览记录失败', 'error');
                }
                console.error('Error loading browse history:', err);
            })
            .finally(() => {
                if (UIController && typeof UIController.hideLoading === 'function') {
                    UIController.hideLoading();
                }
            });
    } else {
        container.innerHTML = '<div class="text-center text-gray-500 p-4">无法加载浏览记录，API不可用</div>';
        if (UIController && typeof UIController.hideLoading === 'function') {
            UIController.hideLoading();
        }
    }
}

// 清空浏览记录
function clearBrowseHistory() {
    // 显示确认对话框
    if (UIController && typeof UIController.showConfirm === 'function') {
        UIController.showConfirm('确定要清空所有浏览记录吗？此操作不可恢复！', () => {
            // 用户确认清空
            performClearBrowseHistory();
        });
    } else {
        // 如果没有确认对话框功能，直接询问
        if (confirm('确定要清空所有浏览记录吗？此操作不可恢复！')) {
            performClearBrowseHistory();
        }
    }
}

// 执行清空浏览记录操作
function performClearBrowseHistory() {
    // 显示加载状态
    if (UIController && typeof UIController.showLoading === 'function') {
        UIController.showLoading();
    }

    // 调用API清空浏览记录
    if (ApiService && ApiService.browseHistory && typeof ApiService.browseHistory.clear === 'function') {
        ApiService.browseHistory.clear()
            .then(res => {
                if (res.code === 200) {
                    // 清空成功
                    if (UIController && typeof UIController.showToast === 'function') {
                        UIController.showToast('浏览记录已清空', 'success');
                    }

                    // 重新加载浏览记录（将显示"暂无浏览记录"）
                    loadBrowseHistory();
                } else {
                    if (UIController && typeof UIController.showToast === 'function') {
                        UIController.showToast(res.message || '清空浏览记录失败', 'error');
                    }
                }
            })
            .catch(err => {
                if (UIController && typeof UIController.showToast === 'function') {
                    UIController.showToast(err.message || '清空浏览记录失败', 'error');
                }
                console.error('Error clearing browse history:', err);
            })
            .finally(() => {
                if (UIController && typeof UIController.hideLoading === 'function') {
                    UIController.hideLoading();
                }
            });
    } else {
        // 如果API不可用，回退到旧方法
        const xhr = new XMLHttpRequest();
        xhr.open('POST', '/Cloud/browse-history/clear', true);
        xhr.setRequestHeader('Content-Type', 'application/json');

        // 添加授权头
        const token = localStorage.getItem('token') || sessionStorage.getItem('token');
        if (token) {
            xhr.setRequestHeader('Authorization', `Bearer ${token}`);
        }

        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                // 隐藏加载状态
                if (UIController && typeof UIController.hideLoading === 'function') {
                    UIController.hideLoading();
                }

                if (xhr.status === 200) {
                    try {
                        const response = JSON.parse(xhr.responseText);
                        if (response.code === 200) {
                            // 清空成功，清空浏览记录展示区域
                            if (UIController && typeof UIController.showToast === 'function') {
                                UIController.showToast('浏览记录已清空', 'success');
                            }

                            // 重新加载浏览记录
                            loadBrowseHistory();
                        } else {
                            if (UIController && typeof UIController.showToast === 'function') {
                                UIController.showToast(response.message || '清空浏览记录失败', 'error');
                            }
                        }
                    } catch (e) {
                        console.error('Error parsing response:', e);
                        if (UIController && typeof UIController.showToast === 'function') {
                            UIController.showToast('清空浏览记录失败', 'error');
                        }
                    }
                } else {
                    if (UIController && typeof UIController.showToast === 'function') {
                        UIController.showToast('清空浏览记录失败', 'error');
                    }
                }
            }
        };

        xhr.send();
    }
}

// 删除单条浏览记录
function deleteBrowseHistory(id, event) {
    // 阻止事件冒泡
    event.stopPropagation();

    // 显示确认对话框
    if (UIController && typeof UIController.showConfirm === 'function') {
        UIController.showConfirm('确定要删除此条浏览记录吗？', () => {
            // 用户确认删除
            performDeleteBrowseHistory(id);
        });
    } else {
        // 如果没有确认对话框功能，直接询问
        if (confirm('确定要删除此条浏览记录吗？')) {
            performDeleteBrowseHistory(id);
        }
    }
}

// 执行删除单条浏览记录操作
function performDeleteBrowseHistory(id) {
    // 显示加载状态
    if (UIController && typeof UIController.showLoading === 'function') {
        UIController.showLoading();
    }

    // 调用API删除浏览记录
    if (ApiService && ApiService.browseHistory && typeof ApiService.browseHistory.delete === 'function') {
        ApiService.browseHistory.delete(id)
            .then(res => {
                if (res.code === 200) {
                    // 删除成功
                    if (UIController && typeof UIController.showToast === 'function') {
                        UIController.showToast('删除成功', 'success');
                    }

                    // 重新加载浏览记录
                    loadBrowseHistory();
                } else {
                    if (UIController && typeof UIController.showToast === 'function') {
                        UIController.showToast(res.message || '删除失败', 'error');
                    }
                }
            })
            .catch(err => {
                if (UIController && typeof UIController.showToast === 'function') {
                    UIController.showToast(err.message || '删除失败', 'error');
                }
                console.error('Error deleting browse history:', err);
            })
            .finally(() => {
                if (UIController && typeof UIController.hideLoading === 'function') {
                    UIController.hideLoading();
                }
            });
    } else {
        // 如果API不可用，回退到旧方法
        const xhr = new XMLHttpRequest();
        xhr.open('DELETE', `/Cloud/browse-history/${id}`, true);
        xhr.setRequestHeader('Content-Type', 'application/json');

        // 添加授权头
        const token = localStorage.getItem('token') || sessionStorage.getItem('token');
        if (token) {
            xhr.setRequestHeader('Authorization', `Bearer ${token}`);
        }

        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                // 隐藏加载状态
                if (UIController && typeof UIController.hideLoading === 'function') {
                    UIController.hideLoading();
                }

                if (xhr.status === 200) {
                    try {
                        const response = JSON.parse(xhr.responseText);
                        if (response.code === 200) {
                            // 删除成功
                            if (UIController && typeof UIController.showToast === 'function') {
                                UIController.showToast('删除成功', 'success');
                            }

                            // 重新加载浏览记录
                            loadBrowseHistory();
                        } else {
                            if (UIController && typeof UIController.showToast === 'function') {
                                UIController.showToast(response.message || '删除失败', 'error');
                            }
                        }
                    } catch (e) {
                        console.error('Error parsing response:', e);
                        if (UIController && typeof UIController.showToast === 'function') {
                            UIController.showToast('删除失败', 'error');
                        }
                    }
                } else {
                    if (UIController && typeof UIController.showToast === 'function') {
                        UIController.showToast('删除失败', 'error');
                    }
                }
            }
        };

        xhr.send();
    }
}

// 记录浏览历史
function recordBrowseHistory(articleId) {
    if (!articleId) {
        console.error('Missing articleId in recordBrowseHistory');
        return;
    }

    // 使用ApiService记录浏览历史（如果可用）
    if (ApiService && ApiService.browseHistory && typeof ApiService.browseHistory.record === 'function') {
        // 这个API可能需要你添加到ApiService中，因为在当前的ApiService中看不到这个方法
        ApiService.browseHistory.record(articleId)
            .catch(err => {
                console.error('Error recording browse history:', err);
            });
    } else {
        // 回退到旧方法
        const xhr = new XMLHttpRequest();
        xhr.open('POST', `/Cloud/browse-history/record/${articleId}`, true);
        xhr.setRequestHeader('Content-Type', 'application/json');

        // 添加授权头
        const token = localStorage.getItem('token') || sessionStorage.getItem('token');
        if (token) {
            xhr.setRequestHeader('Authorization', `Bearer ${token}`);
        }

        xhr.send(JSON.stringify({ articleId: articleId }));
    }
}

// 将函数暴露给全局作用域
window.loadBrowseHistory = loadBrowseHistory;
window.clearBrowseHistory = clearBrowseHistory;
window.deleteBrowseHistory = deleteBrowseHistory;
window.recordBrowseHistory = recordBrowseHistory;