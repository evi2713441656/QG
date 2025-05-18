function replyComment(commentId) {
    // 实现回复评论功能
}

// // 加载评论列表
// function loadComments(articleId) {
//     const commentsContainer = document.getElementById('comments-container');
//     if (!commentsContainer) return;
//
//     // 显示加载中
//     commentsContainer.innerHTML = '<div class="text-center p-4">加载评论中...</div>';
//
//     const xhr = new XMLHttpRequest();
//     xhr.open('GET', `/Cloud/comment/article/${articleId}`, true);
//     xhr.setRequestHeader('Content-Type', 'application/json');
//     xhr.onreadystatechange = function() {
//         if (xhr.readyState === 4) {
//             if (xhr.status === 200) {
//                 try {
//                     const response = JSON.parse(xhr.responseText);
//                     if (response.code === 200) {
//                         commentsContainer.innerHTML = '';
//                         if (!response.data || response.data.length === 0) {
//                             commentsContainer.innerHTML = '<div class="text-center text-gray-500 p-4">暂无评论，快来发表第一条评论吧！</div>';
//                             return;
//                         }
//                         // 渲染评论列表
//                         response.data.forEach(comment => {
//                             commentsContainer.appendChild(createCommentItem(comment));
//                         });
//                     } else {
//                         commentsContainer.innerHTML = '<div class="text-center text-red-500 p-4">加载评论失败: ' + response.message + '</div>';
//                     }
//                 } catch (e) {
//                     commentsContainer.innerHTML = '<div class="text-center text-red-500 p-4">解析评论数据失败</div>';
//                     console.error('解析评论数据失败:', e);
//                 }
//             } else {
//                 alert('获取评论列表失败: ' + response.message);
//             }
//         }
//     };
//     xhr.send();
// }
//
// // 创建评论项
// function createCommentItem(comment) {
//     const commentDiv = document.createElement('div');
//     commentDiv.className = 'bg-gray-50 rounded p-4 mb-4';
//     commentDiv.setAttribute('data-id', comment.id);
//
//     // 格式化时间
//     const createTime = new Date(comment.createTime || Date.now()).toLocaleString();
//
//     commentDiv.innerHTML = `
//         <div class="flex items-start">
//             <img src="${comment.userAvatar || 'https://picsum.photos/30/30'}" alt="用户头像" class="w-10 h-10 rounded-full mr-3">
//             <div class="flex-1">
//                 <div class="flex items-center">
//                     <h4 class="font-bold">${comment.userName || '匿名用户'}</h4>
//                     <span class="text-gray-500 text-sm ml-2">${createTime}</span>
//                 </div>
//                 <p class="my-2">${comment.content}</p>
//                 <button class="text-blue-500 text-sm" onclick="showReplyForm(${comment.id})">回复</button>
//
//                 <div id="reply-form-${comment.id}" class="hidden mt-3">
//                     <textarea class="w-full border rounded p-2 mb-2" placeholder="写下你的回复..."></textarea>
//                     <div class="flex justify-end space-x-2">
//                         <button class="px-4 py-1 bg-gray-300 rounded" onclick="hideReplyForm(${comment.id})">取消</button>
//                         <button class="px-4 py-1 bg-blue-500 text-white rounded" onclick="submitReply(${comment.id})">回复</button>
//                     </div>
//                 </div>
//             </div>
//         </div>
//     `;
//
//     // 如果有子评论，递归添加
//     if (comment.children && comment.children.length > 0) {
//         const childrenContainer = document.createElement('div');
//         childrenContainer.className = 'ml-12 mt-3';
//
//         comment.children.forEach(childComment => {
//             childrenContainer.appendChild(createCommentItem(childComment));
//         });
//
//         commentDiv.appendChild(childrenContainer);
//     }
//
//     return commentDiv;
// }

// 渲染评论树形结构
function renderCommentTree(container, comments, parentId = null) {
    // 筛选当前层级的评论
    const currentLevelComments = comments.filter(comment => comment.parentId === parentId);

    // 如果没有评论，返回
    if (currentLevelComments.length === 0) return;

    // 创建评论列表容器
    const list = parentId === null ? container : document.createElement('div');
    if (parentId !== null) {
        list.className = 'pl-8 mt-2';
    }

    // 遍历评论
    currentLevelComments.forEach(comment => {
        // 创建评论项
        const item = document.createElement('div');
        item.className = 'comment-item bg-gray-50 p-3 rounded mb-3';

        // 格式化时间
        const createTime = new Date(comment.createTime).toLocaleString();

        // 设置评论内容
        item.innerHTML = `
      <div class="flex items-start mb-2">
        <img src="${comment.userAvatar || 'https://picsum.photos/30/30'}" alt="用户头像" class="rounded-full w-8 h-8 mr-2">
        <div>
          <div class="flex items-center">
            <h4 class="text-md font-bold">${comment.userName}</h4>
            <span class="text-gray-500 text-xs ml-2">${createTime}</span>
          </div>
          <p class="text-gray-700">${comment.content}</p>
        </div>
      </div>
      <div class="flex justify-end">
        <button class="text-sm text-blue-500" onclick="showReplyForm(${comment.id})">回复</button>
      </div>
      <div id="reply-form-${comment.id}" class="reply-form hidden mt-2">
        <textarea class="w-full p-2 border rounded" placeholder="写下你的回复..."></textarea>
        <div class="flex justify-end mt-2">
          <button class="bg-gray-300 text-gray-700 px-3 py-1 rounded mr-2" onclick="hideReplyForm(${comment.id})">取消</button>
          <button class="bg-blue-500 text-white px-3 py-1 rounded" onclick="submitReply(${comment.id})">回复</button>
        </div>
      </div>
    `;

        // 添加到列表容器
        list.appendChild(item);

        // 递归渲染子评论
        renderCommentTree(item, comments, comment.id);
    });

    // 如果不是根容器，添加到父节点
    if (parentId !== null) {
        container.appendChild(list);
    }
}

// 显示回复表单
function showReplyForm(commentId) {
    const replyForm = document.getElementById(`reply-form-${commentId}`);
    if (replyForm) {
        replyForm.classList.remove('hidden');
    }
}

// 隐藏回复表单
function hideReplyForm(commentId) {
    const replyForm = document.getElementById(`reply-form-${commentId}`);
    if (replyForm) {
        replyForm.classList.add('hidden');
    }
}

// 提交回复
function submitReply(commentId) {
    const replyForm = document.getElementById(`reply-form-${commentId}`);
    if (!replyForm) return;

    const textarea = replyForm.querySelector('textarea');
    if (!textarea) return;

    const content = textarea.value.trim();
    if (!content) {
        alert('请输入回复内容');
        return;
    }

    const articleId = getCurrentArticleId();
    if (!articleId) {
        alert('未找到文章ID');
        return;
    }

    // 发送请求
    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/Cloud/comment', true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    if (response.code === 200) {
                        // 清空输入框并隐藏回复表单
                        textarea.value = '';
                        hideReplyForm(commentId);

                        // 重新加载评论列表
                        loadComments(articleId);

                        if (UIController && typeof UIController.showToast === 'function') {
                            UIController.showToast('回复成功', 'success');
                        } else {
                            alert('回复成功');
                        }
                    } else {
                        alert('发表回复失败: ' + response.message);
                    }
                } catch (e) {
                    alert('解析响应失败');
                    console.error('解析响应失败:', e);
                }
            } else {
                alert('发表回复失败');
            }
        }
    };

    const data = JSON.stringify({
        articleId: articleId,
        parentId: commentId,
        content: content
    });

    xhr.send(data);
}

// 提交评论
function submitComment() {
    const commentInput = document.getElementById('comment-input');
    if (!commentInput) return;

    const content = commentInput.value.trim();
    if (!content) {
        alert('请输入评论内容');
        return;
    }

    const articleId = getCurrentArticleId();
    if (!articleId) {
        alert('未找到文章ID');
        return;
    }

    // 发送请求
    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/Cloud/comment', true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    if (response.code === 200) {
                        // 清空输入框
                        commentInput.value = '';

                        // 重新加载评论列表
                        loadComments(articleId);

                        if (UIController && typeof UIController.showToast === 'function') {
                            UIController.showToast('评论成功', 'success');
                        } else {
                            alert('评论成功');
                        }
                    } else {
                        alert('发表评论失败: ' + response.message);
                    }
                } catch (e) {
                    alert('解析响应失败');
                    console.error('解析响应失败:', e);
                }
            } else {
                alert('发表评论失败');
            }
        }
    };

    const data = JSON.stringify({
        articleId: articleId,
        parentId: null,
        content: content
    });

    xhr.send(data);
}

// 加载文章评论，支持分页
let currentCommentPage = 1;
let commentsPerPage = 10;
let totalCommentPages = 1;

// 加载评论列表
function loadComments(articleId) {
    const commentsContainer = document.getElementById('comments-container');
    if (!commentsContainer) return;

    // 显示加载中
    commentsContainer.innerHTML = '<div class="text-center p-4">加载评论中...</div>';

    const xhr = new XMLHttpRequest();
    xhr.open('GET', `/Cloud/comment/article/${articleId}`, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    if (response.code === 200) {
                        commentsContainer.innerHTML = '';
                        if (!response.data || response.data.length === 0) {
                            commentsContainer.innerHTML = '<div class="text-center text-gray-500 p-4">暂无评论，快来发表第一条评论吧！</div>';
                            return;
                        }
                        // 渲染评论列表
                        response.data.forEach(comment => {
                            commentsContainer.appendChild(createCommentItem(comment));
                        });
                    } else {
                        commentsContainer.innerHTML = '<div class="text-center text-red-500 p-4">加载评论失败: ' + response.message + '</div>';
                    }
                } catch (e) {
                    commentsContainer.innerHTML = '<div class="text-center text-red-500 p-4">解析评论数据失败</div>';
                    console.error('解析评论数据失败:', e);
                }
            } else {
                alert('获取评论列表失败: ' + response.message);
            }
        }
    };
    xhr.send();
}

// 创建评论项
function createCommentItem(comment) {
    const commentDiv = document.createElement('div');
    commentDiv.className = 'bg-gray-50 rounded p-4 mb-4';
    commentDiv.setAttribute('data-id', comment.id);

    // 格式化时间
    const createTime = new Date(comment.createTime || Date.now()).toLocaleString();

    commentDiv.innerHTML = `
        <div class="flex items-start">
            <img src="${comment.userAvatar || 'https://picsum.photos/30/30'}" alt="用户头像" class="w-10 h-10 rounded-full mr-3">
            <div class="flex-1">
                <div class="flex items-center">
                    <h4 class="font-bold">${comment.userName || '匿名用户'}</h4>
                    <span class="text-gray-500 text-sm ml-2">${createTime}</span>
                </div>
                <p class="my-2">${comment.content}</p>
                <button class="text-blue-500 text-sm" onclick="showReplyForm(${comment.id})">回复</button>
                
                <div id="reply-form-${comment.id}" class="hidden mt-3">
                    <textarea class="w-full border rounded p-2 mb-2" placeholder="写下你的回复..."></textarea>
                    <div class="flex justify-end space-x-2">
                        <button class="px-4 py-1 bg-gray-300 rounded" onclick="hideReplyForm(${comment.id})">取消</button>
                        <button class="px-4 py-1 bg-blue-500 text-white rounded" onclick="submitReply(${comment.id})">回复</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    // 如果有子评论，递归添加
    if (comment.children && comment.children.length > 0) {
        const childrenContainer = document.createElement('div');
        childrenContainer.className = 'ml-12 mt-3';

        comment.children.forEach(childComment => {
            childrenContainer.appendChild(createCommentItem(childComment));
        });

        commentDiv.appendChild(childrenContainer);
    }

    return commentDiv;
}

function createRepliesList(replies) {
    let html = '<div class="replies mt-2 pl-5 border-l-2 border-gray-200">';

    replies.forEach(reply => {
        const replyTime = new Date(reply.createTime).toLocaleString();

        html += `
      <div class="reply-item py-2">
        <div class="flex items-center">
          <span class="font-medium">${reply.authorName}</span>
          <span class="text-gray-500 text-xs ml-2">${replyTime}</span>
        </div>
        <div class="mt-1">${reply.content}</div>
        <div class="mt-1 text-sm">
          <a href="javascript:void(0);" class="text-blue-500 mr-2" onclick="showReplyForm(${reply.parentId}, ${reply.id})">回复</a>
          ${reply.authorId === getCurrentUserId() ?
            `<a href="javascript:void(0);" class="text-red-500" onclick="deleteReply(${reply.id})">删除</a>` :
            ''}
        </div>
      </div>
    `;
    });

    html += '</div>';
    return html;
}

