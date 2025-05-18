// 全局变量
let currentArticlePage = 1;
let articlesPerPage = 10;
let totalArticlePages = 1;

// 创建文章卡片函数
function createArticleCard(article) {
    const card = document.createElement('div');
    card.className = 'article-card border-b pb-4';

    // Format date
    const publishTime = new Date(article.createTime).toLocaleString();

    // Set card content
    card.innerHTML = `
    <h3 class="text-lg font-semibold">
      <a href="javascript:void(0);" onclick="showArticleDetail(${article.id})">${article.title}</a>
    </h3>
    <p class="text-sm text-gray-500">作者：${article.authorName || '未知'} | 发布时间：${publishTime}</p>
    <p class="mt-2 text-gray-700 line-clamp-2">${getArticleSummary(article.content)}</p>
    <div class="flex items-center mt-2 space-x-4">
      <span class="text-sm text-gray-500"><i class="far fa-eye"></i> ${article.viewCount}</span>
      <span class="text-sm text-gray-500"><i class="far fa-thumbs-up"></i> ${article.likeCount}</span>
      <span class="text-sm text-gray-500"><i class="far fa-comment"></i> ${article.commentCount}</span>
    </div>
  `;

    return card;
}

function getArticleSummary(content) {
    if (!content) return '';

    // Remove HTML tags
    const plainText = content.replace(/<[^>]+>/g, '');

    // Truncate to 100 characters
    return plainText.length > 100 ? plainText.substring(0, 100) + '...' : plainText;
}

function saveArticle() {
    const title = document.getElementById('article-title').value;
    const content = document.getElementById('editor-content').innerHTML;
    const author = '当前用户'; // 从用户信息中获取
    const createTime = new Date().toLocaleString();

    // 验证表单数据
    if (!title.trim()) {
        UIController.showToast('请输入文章标题', 'error');
        return;
    }

    if (!content.trim()) {
        UIController.showToast('请输入文章内容', 'error');
        return;
    }

    // 判断是新建还是更新
    if (currentEditingArticleId) {
        // 更新文章
        updateArticle(currentEditingArticleId);
    } else {
        // 新建文章
        createArticle();
    }
}

// 发起 AJAX 请求获取最近发表的文章
function getRecentArticles() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', '/Cloud/article/recent', true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    // 添加授权头
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    if (token) {
        xhr.setRequestHeader('Authorization', `Bearer ${token}`);
    }

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                const response = JSON.parse(xhr.responseText);
                if (response.code === 200) {
                    const articles = response.data;
                    const articleList = document.getElementById('recent-articles');
                    if (articleList) {
                        articleList.innerHTML = '';
                        articles.forEach(article => {
                            const articleDiv = document.createElement('div');
                            articleDiv.innerHTML = `
                                <h3 class="text-lg font-bold">${article.title}</h3>
                                <p class="text-gray-600">作者：${article.author} | 创建时间：${new Date(article.createTime).toLocaleString()}</p>
                                <p>${article.content.slice(0, 100)}...</p>
                                <a href="#" onclick="showArticleDetail(${article.id})">查看全文</a>
                            `;
                            articleList.appendChild(articleDiv);
                        });
                    }
                } else {
                    console.error('获取最近文章失败:', response.message);
                }
            } else if (xhr.status === 401) {
                alert("未登录或会话已过期，请重新登录");
                window.location.href = 'login.html';
            } else {
                console.error("Error loading articles:", xhr.status);
            }
        }
    };
    xhr.send();
}
window.onload = getRecentArticles;

function getArticlesByPage(page, sortBy = 'createTime') {
    // 显示加载状态
    UIController.showLoading();

    // 创建 XMLHttpRequest 对象
    const xhr = new XMLHttpRequest();
    // 配置请求，使用 GET 方法请求指定页面和排序方式的文章列表
    xhr.open('GET', `/Cloud/article/recent?page=${page}&sortBy=${sortBy}`, true);
    // 设置请求头，指定请求内容类型为 JSON
    xhr.setRequestHeader('Content-Type', 'application/json');

    // 添加授权头
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    if (token) {
        // 如果存在 token，将其添加到请求头的 Authorization 字段中
        xhr.setRequestHeader('Authorization', `Bearer ${token}`);
    }

    // 监听请求状态变化
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            // 隐藏加载状态
            UIController.hideLoading();
            if (xhr.status === 200) {
                const response = JSON.parse(xhr.responseText);
                if (response.code === 200) {
                    const articles = response.data;
                    const articleList = document.getElementById('recent-articles');
                    if (articleList) {
                        // 清空文章列表容器
                        articleList.innerHTML = '';
                        if (articles.length === 0) {
                            // 如果没有文章，显示提示信息
                            articleList.innerHTML = '<div class="text-center text-gray-500 p-4">暂无最近发表的文章</div>';
                        } else {
                            // 遍历文章数据
                            articles.forEach(article => {
                                const articleDiv = document.createElement('div');
                                // 设置文章项的 HTML 内容
                                articleDiv.innerHTML = `
                                    <h3 class="text-lg font-bold">${article.title}</h3>
                                    <p class="text-gray-600">作者：${article.author} | 创建时间：${new Date(article.createTime).toLocaleString()}</p>
                                    <p>${article.content.slice(0, 100)}...</p>
                                    <a href="#" onclick="showArticleDetail(${article.id})">查看全文</a>
                                `;
                                // 将文章项添加到文章列表容器中
                                articleList.appendChild(articleDiv);
                            });
                        }
                    }

                    // 更新分页按钮
                    const paginationDiv = document.getElementById('article-pagination');
                    if (paginationDiv) {
                        // 清空分页按钮容器
                        paginationDiv.innerHTML = '';
                        // 计算总页数
                        const totalPages = Math.ceil(response.total / response.size);
                        for (let i = 1; i <= totalPages; i++) {
                            const button = document.createElement('button');
                            button.textContent = i;
                            // 根据当前页码设置按钮的样式
                            button.className = 'pagination-btn ' + (i === page ? 'active' : '');
                            if (i === page) {
                                // 当前页码对应的按钮禁用
                                button.disabled = true;
                            }
                            // 为按钮添加点击事件，点击时调用 getArticlesByPage 函数加载相应页面的文章
                            button.addEventListener('click', () => getArticlesByPage(i, sortBy));
                            // 将按钮添加到分页按钮容器中
                            paginationDiv.appendChild(button);
                        }
                    }
                } else {
                    // 服务器返回的 code 不为 200，打印错误信息
                    console.error('获取最近文章失败:', response.message);
                    UIController.showToast(response.message || '获取最近文章失败', 'error');
                }
            } else if (xhr.status === 401) {
                // 未授权 - 重定向到登录页面
                alert("未登录或会话已过期，请重新登录");
                window.location.href = 'login.html';
            } else {
                // 其他状态码，打印错误信息
                console.error("Error loading articles:", xhr.status);
                UIController.showToast('加载文章失败，请稍后重试', 'error');
            }
        }
    };
    // 发送请求
    xhr.send();
}
window.onload = () => getArticlesByPage(1);

// 加载最近发表的文章
function loadRecentArticles(sortBy = 'createTime') {
    UIController.showLoading();

    ApiService.article.getLatest()
        .then(res => {
            if (res.code === 200 && res.data) {
                // 获取最近文章容器
                const container = document.getElementById('recent-articles');
                container.innerHTML = '';

                // 如果没有文章，显示提示
                if (res.data.length === 0) {
                    container.innerHTML = '<div class="text-center text-gray-500 p-4">暂无最近发表的文章</div>';
                    return;
                }

                // 如果排序方式为点赞数，则重新排序
                if (sortBy === 'likes') {
                    res.data.sort((a, b) => b.likeCount - a.likeCount);
                }

                // 渲染文章列表
                res.data.forEach(article => {
                    container.appendChild(createArticleItem(article));
                });

                // 生成分页按钮
                generatePagination(res.total, res.current, res.size, 'article-pagination', page => {
                    loadRecentArticles(sortBy, page);
                });
            } else {
                UIController.showToast(res.message || '加载最近发表的文章失败', 'error');
            }
        })
        .catch(err => {
            UIController.showToast(err.message || '加载最近发表的文章失败', 'error');
        })
        .finally(() => {
            UIController.hideLoading();
        });
}
window.onload = loadRecentArticles;

// 按点赞数排序文章
function sortArticles() {
    const sortSelect = document.getElementById('sort-select');
    const sortValue = sortSelect ? sortSelect.value : 'createTime';
    loadArticles(1, sortValue);
}

// 创建文章列表项
function createArticleItem(article) {
    const item = document.createElement('div');
    item.className = 'article-item p-2 hover:bg-gray-100 rounded cursor-pointer';
    item.setAttribute('data-id', article.id);

    // 设置点击文章进入详情页
    item.onclick = () => showArticleDetail(article.id);

    // 格式化时间
    const createTime = new Date(article.createTime).toLocaleString();

    // 设置列表项内容
    item.innerHTML = `
        <h4 class="text-md font-bold">${article.title}</h4>
        <p class="text-gray-600 text-sm">作者：${article.authorName || '未知'} | 发表时间：${createTime}</p>
        <p class="text-gray-600 text-sm">点赞：<span class="like-count">${article.likeCount || 0}</span> | 评论：${article.commentCount || 0}</p>
    `;

    return item;
}

function loadArticles(page = 1, sortBy = 'createTime') {
    // Show loading state
    UIController.showLoading();

    // Update current page
    currentArticlePage = page;

    // Call the API to get articles with pagination
    fetch(`/Cloud/article/recent?page=${page}&size=${articlesPerPage}&sortBy=${sortBy}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.code === 200) {
                const articlesContainer = document.getElementById('recent-articles');
                articlesContainer.innerHTML = '';

                if (!data.data || data.data.length === 0) {
                    articlesContainer.innerHTML = '<div class="text-center text-gray-500 p-4">暂无文章</div>';
                    return;
                }

                // Render articles
                data.data.forEach(article => {
                    articlesContainer.appendChild(createArticleCard(article));
                });

                // Create pagination controls
                createPagination(page, totalArticlePages, loadArticles, 'article-pagination', sortBy);
            } else {
                UIController.showToast(data.message || '加载文章失败', 'error');
            }
        })
        .catch(err => {
            console.error('Error loading articles:', err);
            UIController.showToast('加载文章失败，请稍后重试', 'error');
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// 加载文章列表
function loadArticleList(knowledgeBaseId) {
    ApiService.article.getList(knowledgeBaseId)
        .then(res => {
            console.log(res);
            if (res.code === 200 && res.data) {
                const articleList = document.getElementById('article-list');
                articleList.innerHTML = '';

                if (!res.data) {
                    articleList.innerHTML = '<div class="text-center text-gray-500 p-4">暂无文章，点击新建按钮创建第一篇文章</div>';
                    return;
                }

                res.data.forEach(article => {
                    const articleDiv = document.createElement('div');
                    articleDiv.classList.add('article', 'p-4', 'border-b', 'border-gray-300', 'cursor-pointer', 'hover:bg-gray-100');
                    articleDiv.onclick = () => showArticleDetail(article.id);

                    // 格式化时间
                    const createTime = new Date(article.createTime || Date.now()).toLocaleString();

                    articleDiv.innerHTML = `
                        <h3 class="text-lg font-bold">${article.title}</h3>
                        <p class="text-gray-600 text-sm">作者：${article.authorName || '未知作者'} | 发表时间：${createTime}</p>
                        <p class="text-gray-600 text-sm">点赞：${article.likeCount || 0} | 评论：${article.commentCount || 0}</p>
                    `;

                    articleList.appendChild(articleDiv);
                });
            } else {
                UIController.showToast(res.message || '加载文章列表失败', 'error');
            }
        })
        .catch(err => {
            UIController.showToast(err.message || '加载文章列表失败', 'error');
        });
}

// 显示文章编辑器
function showArticleEditor(articleId = null) {
    if (!currentKnowledgeBaseId) {
        UIController.showToast('请先选择知识库', 'error');
        return;
    }

    // 显示文章编辑器页面
    UIController.showPage('article-editor');

    // 如果有文章ID，表示编辑现有文章
    if (articleId) {
        // 加载文章内容
        UIController.showLoading();

        ApiService.article.getDetail(articleId)
            .then(res => {
                if (res.code === 200 && res.data) {
                    // 填充编辑器
                    document.getElementById('article-title').value = res.data.title;
                    document.getElementById('editor-content').innerHTML = res.data.content;

                    // 设置表单提交处理
                    document.getElementById('article-form').onsubmit = (e) => {
                        e.preventDefault();
                        updateArticle(articleId);
                    };
                } else {
                    UIController.showToast(res.message || '加载文章失败', 'error');
                }
            })
            .catch(err => {
                UIController.showToast(err.message || '加载文章失败', 'error');
            })
            .finally(() => {
                UIController.hideLoading();
            });
    } else {
        // 新建文章，清空编辑器
        document.getElementById('article-title').value = '';
        document.getElementById('editor-content').innerHTML = '';

        // 重置当前编辑的文章ID
        currentEditingArticleId = null;
        // // 设置表单提交处理
        // document.getElementById('article-form').onsubmit = (e) => {
        //     e.preventDefault();
        //     createArticle();
        // };
    }
}

// 创建文章
function createArticle() {
    // 获取表单数据
    const title = document.getElementById('article-title').value;
    const content = document.getElementById('editor-content').innerHTML;

    // 表单验证
    if (!title) {
        UIController.showToast('请输入文章标题', 'error');
        return;
    }

    if (!content) {
        UIController.showToast('请输入文章内容', 'error');
        return;
    }

    // 显示加载状态
    UIController.showLoading();

    // 调用创建文章API
    ApiService.article.create(currentKnowledgeBaseId, title, content)
        .then(res => {
            console.log('准备发送的数据:', currentKnowledgeBaseId, title, content); // 调试日志
            if (res.code === 200) {
                UIController.showToast('创建文章成功', 'success');

                // 返回知识库详情页
                showKnowledgeDetail(currentKnowledgeBaseId);
            } else {
                UIController.showToast(res.message || '创建文章失败', 'error');
            }
        })
        .catch(err => {
            UIController.showToast(err.message || '创建文章失败', 'error');
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// 更新文章
function updateArticle(articleId) {
    // 获取表单数据
    const title = document.getElementById('article-title').value;
    const content = document.getElementById('editor-content').innerHTML;

    // 表单验证
    if (!title) {
        UIController.showToast('请输入文章标题', 'error');
        return;
    }

    if (!content) {
        UIController.showToast('请输入文章内容', 'error');
        return;
    }

    // 显示加载状态
    UIController.showLoading();

    // 调用更新文章API
    ApiService.article.update(articleId, title, content)
        .then(res => {
            if (res.code === 200) {
                UIController.showToast('更新文章成功', 'success');

                // 返回知识库详情页
                showKnowledgeDetail(currentKnowledgeBaseId);
            } else {
                UIController.showToast(res.message || '更新文章失败', 'error');
            }
        })
        .catch(err => {
            UIController.showToast(err.message || '更新文章失败', 'error');
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// 取消编辑文章
function cancelArticle() {
    // 确认取消
    UIController.showConfirm('确定要取消编辑吗？未保存的内容将丢失！', () => {
        // 返回知识库详情页
        showKnowledgeDetail(currentKnowledgeBaseId);
    });
}

// 显示文章详情
function showArticleDetail(articleId) {
    // 记录浏览历史
    if (typeof recordBrowseHistory === 'function') {
        recordBrowseHistory(articleId);
    }
    UIController.showLoading();

    fetch(`/Cloud/article/${articleId}`)
        .then(response => response.json())
        .then(data => {
            if (data.code === 200 && data.data) {
                // 显示文章详情页
                UIController.showPage('article-detail');

                // 填充文章信息
                const article = data.data;
                document.getElementById('article-title-display').textContent = article.title;
                document.getElementById('article-author-display').textContent = article.authorName || '未知';
                document.getElementById('article-time-display').textContent = new Date(article.createTime).toLocaleString();
                document.getElementById('article-content-display').innerHTML = article.content;
                document.getElementById('article-like-count').textContent = article.likeCount;

                // 设置点赞和收藏按钮状态
                const likeButton = document.getElementById('article-like-button');
                likeButton.dataset.id = article.id;
                likeButton.dataset.liked = article.liked ? 'true' : 'false';
                likeButton.className = article.liked ? 'fas fa-thumbs-up like-button liked' : 'far fa-thumbs-up like-button';

                const favoriteButton = document.getElementById('article-favorite-button');
                favoriteButton.dataset.id = article.id;
                favoriteButton.className = article.favorited ? 'fas fa-heart favorite-button favorited' : 'far fa-heart favorite-button';

                // 加载评论
                loadComments(articleId);
            } else {
                UIController.showToast(data.message || '加载文章详情失败', 'error');
            }
        })
        .catch(err => {
            UIController.showToast('加载文章详情失败', 'error');
            console.error('加载文章详情失败:', err);
        })
        .finally(() => {
            UIController.hideLoading();
        });
}

// 切换文章点赞状态
function toggleArticleLike(event) {
    event.stopPropagation();
    const likeButton = document.getElementById('article-like-button');
    const likeCount = document.getElementById('article-like-count');
    const articleId = likeButton.getAttribute('data-id');
    if (!articleId) {
        console.error('未找到文章ID');
        return;
    }
    const isLiked = likeButton.getAttribute('data-liked') === 'true';
    const currentCount = parseInt(likeCount.textContent) || 0;
    // 先更新UI
    if (isLiked) {
        likeButton.classList.remove('fas', 'liked');
        likeButton.classList.add('far');
        likeButton.setAttribute('data-liked', 'false');
        likeCount.textContent = Math.max(0, currentCount - 1);
    } else {
        likeButton.classList.remove('far');
        likeButton.classList.add('fas', 'liked');
        likeButton.setAttribute('data-liked', 'true');
        likeCount.textContent = currentCount + 1;
    }
    // 发送请求
    const xhr = new XMLHttpRequest();
    xhr.open('POST', `/Cloud/article/${isLiked ? 'unlike' : 'like'}/${articleId}`, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status !== 200) {
                console.log(xhr.responseText);
                // 如果请求失败，回滚UI更改
                if (isLiked) {
                    likeButton.classList.remove('far');
                    likeButton.classList.add('fas', 'liked');
                    likeButton.setAttribute('data-liked', 'true');
                    likeCount.textContent = currentCount;
                } else {
                    likeButton.classList.remove('fas', 'liked');
                    likeButton.classList.add('far');
                    likeButton.setAttribute('data-liked', 'false');
                    likeCount.textContent = currentCount;
                }

                alert('你已点赞过此文章');
            }
        }
    };
    xhr.send();
}

// 切换文章收藏状态
function toggleArticleFavorite(event) {
    event.stopPropagation();
    const favoriteButton = document.getElementById('article-favorite-button');
    const articleId = favoriteButton.getAttribute('data-id');
    if (!articleId) {
        console.error('未找到文章ID');
        return;
    }
    const isFavorite = favoriteButton.classList.contains('fas');
    if (isFavorite) {
        favoriteButton.classList.remove('fas');
        favoriteButton.classList.add('far');
        if (UIController && typeof UIController.showToast === 'function') {
            UIController.showToast('已取消收藏', 'info');
        }
    } else {
        favoriteButton.classList.remove('far');
        favoriteButton.classList.add('fas');
        if (UIController && typeof UIController.showToast === 'function') {
            UIController.showToast('已添加收藏', 'success');
        }
    }
    // 发送请求
    const xhr = new XMLHttpRequest();
    xhr.open('POST', `/Cloud/article/${isFavorite ? 'unfavorite' : 'favorite'}/${articleId}`, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status !== 200) {
                // 如果请求失败，回滚UI更改
                if (isFavorite) {
                    favoriteButton.classList.remove('far');
                    favoriteButton.classList.add('fas');
                } else {
                    favoriteButton.classList.remove('fas');
                    favoriteButton.classList.add('far');
                }

                alert('你已收藏过此文章');
            }
        }
    };
    xhr.send();
}

// // 获取当前文章ID
// function getCurrentArticleId() {
//     // 从URL中获取文章ID
//     const urlParams = new URLSearchParams(window.location.search);
//     return urlParams.get('articleId');
// }
//
// window.onload = function() {
//     getCurrentArticleId();
// }