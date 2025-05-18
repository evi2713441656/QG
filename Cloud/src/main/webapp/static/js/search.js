// Search function - Initiates the search process
function search() {
    // Get the search keyword from the input field
    const searchInput = document.querySelector('input[placeholder="搜索..."]');
    if (!searchInput) {
        console.error("Search input not found");
        return;
    }

    const keyword = searchInput.value.trim();
    if (!keyword) {
        UIController.showToast('请输入搜索关键词', 'warning');
        return;
    }

    // Show loading indicator
    if (UIController && typeof UIController.showLoading === 'function') {
        UIController.showLoading();
    }

    // Create search results page if it doesn't exist
    let searchResultsPage = document.getElementById('search-results-page');
    if (!searchResultsPage) {
        searchResultsPage = document.createElement('div');
        searchResultsPage.id = 'search-results-page';
        searchResultsPage.className = 'flex h-screen hidden';
        searchResultsPage.innerHTML = `
            <div class="bg-gray-800 text-white w-64">
                <div class="p-4">
                    <h2 class="text-xl font-bold">搜索结果</h2>
                    <ul class="mt-4">
                        <li class="p-2 hover:bg-gray-700"><a href="javascript:void(0);" onclick="showPage('homePage')">返回首页</a></li>
                    </ul>
                </div>
            </div>
            <div class="flex-1 flex flex-col">
                <div class="bg-white shadow-md p-4 flex justify-between items-center">
                    <h2 class="text-xl font-bold">搜索结果: "${keyword}"</h2>
                </div>
                <div class="p-4 flex-1 overflow-auto">
                    <div class="mb-4">
                        <div class="flex space-x-4 mb-2">
                            <button class="search-tab py-2 px-4 rounded" data-target="all">全部</button>
                            <button class="search-tab py-2 px-4 rounded" data-target="knowledge">知识库</button>
                            <button class="search-tab py-2 px-4 rounded" data-target="article">文章</button>
                            <button class="search-tab py-2 px-4 rounded" data-target="user">用户</button>
                        </div>
                    </div>
                    <div id="search-results-container"></div>
                </div>
            </div>
        `;
        document.body.appendChild(searchResultsPage);

        // Add active class to the "All" tab initially
        const allTab = searchResultsPage.querySelector('.search-tab[data-target="all"]');
        if (allTab) {
            allTab.classList.add('bg-blue-500', 'text-white');
        }

        // Bind tab click events
        searchResultsPage.querySelectorAll('.search-tab').forEach(tab => {
            tab.addEventListener('click', function() {
                // Remove active class from all tabs
                searchResultsPage.querySelectorAll('.search-tab').forEach(t => {
                    t.classList.remove('bg-blue-500', 'text-white');
                    t.classList.add('bg-gray-200', 'text-gray-700');
                });

                // Add active class to current tab
                this.classList.remove('bg-gray-200', 'text-gray-700');
                this.classList.add('bg-blue-500', 'text-white');

                // Get target type
                const target = this.dataset.target;
                // Perform search for the specified type
                performSearch(keyword, target);
            });
        });
    } else {
        // Update the search keyword in the heading
        const heading = searchResultsPage.querySelector('h2:nth-child(1)');
        if (heading) {
            heading.textContent = `搜索结果: "${keyword}"`;
        }
    }

    // Perform the search
    console.log("Initiating search for:", keyword);
    performSearch(keyword, 'all');

    // Show the search results page
    showPage('search-results-page');
}

// 执行搜索
function performSearch(keyword, type = 'all') {
    const resultsContainer = document.getElementById('search-results-container');
    resultsContainer.innerHTML = '<div class="text-center p-4">搜索中...</div>';

    // Determine the correct endpoint based on search type
    let endpoint = '';
    switch(type) {
        case 'knowledge':
            endpoint = `/Cloud/search/knowledge?keyword=${encodeURIComponent(keyword)}`;
            break;
        case 'article':
            endpoint = `/Cloud/search/article?keyword=${encodeURIComponent(keyword)}`;
            break;
        case 'user':
            endpoint = `/Cloud/search/user?keyword=${encodeURIComponent(keyword)}`;
            break;
        case 'all':
        default:
            endpoint = `/Cloud/search/global?keyword=${encodeURIComponent(keyword)}`;
            break;
    }

    console.log("Searching with endpoint:", endpoint);

    // Use fetch with proper error handling
    fetch(endpoint)
        .then(response => {
            console.log("Search response status:", response.status);
            // Check if the response is ok (status in the range 200-299)
            if (!response.ok) {
                throw new Error(`HTTP error: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log("Search results data:", data);
            if (data.code === 200) {
                resultsContainer.innerHTML = '';

                // Handle the case where data might be null or empty
                if (!data.data || (
                    (!data.data.knowledgeBase && !data.data.article && !data.data.user) &&
                    data.data.total === 0)) {
                    resultsContainer.innerHTML = '<div class="text-center p-4">未找到相关结果</div>';
                    return;
                }

                // Display search results
                displaySearchResults(data.data, resultsContainer);
            } else {
                resultsContainer.innerHTML = `<div class="text-center p-4">未找到相关结果 (${data.message || '没有匹配的内容'})</div>`;
            }
        })
        .catch(error => {
            console.error('Search error:', error);
            resultsContainer.innerHTML = `<div class="text-center p-4 text-red-500">搜索出错：${error.message}</div>`;

            if (error.message.includes('500')) {
                resultsContainer.innerHTML += `
                    <div class="mt-4 p-4 bg-yellow-100 border-l-4 border-yellow-500">
                        <p><strong>开发提示:</strong> 服务器返回了500错误。这可能是因为:</p>
                        <ul class="list-disc pl-5 mt-2">
                            <li>搜索关键字不是有效的数字ID (SearchServiceImpl尝试将关键字转换为Long)</li>
                            <li>后端异常处理机制未正确捕获转换错误</li>
                        </ul>
                    </div>`;
            }
        })
        .finally(() => {
            if (UIController && typeof UIController.hideLoading === 'function') {
                UIController.hideLoading();
            }
        });
}

// Display search results
function displaySearchResults(results, container) {
    // Clear the container first
    container.innerHTML = '';

    // Add debugging info
    console.log("Results object to display:", results);

    // Check if we have search results based on the API response structure
    const isEmpty = !results ||
        (((!results.knowledgeBaseVO || Object.keys(results.knowledgeBaseVO).length === 0) &&
                (!results.articleVO || Object.keys(results.articleVO).length === 0) &&
                (!results.userVO || Object.keys(results.userVO).length === 0)) &&
            results.total === 0);

    if (isEmpty) {
        container.innerHTML = '<div class="text-center p-4">未找到相关结果</div>';
        return;
    }

    // Calculate total count of results
    const totalCount =
        (results.knowledgeBaseVO && Object.keys(results.knowledgeBaseVO).length > 0 ? 1 : 0) +
        (results.articleVO && Object.keys(results.articleVO).length > 0 ? 1 : 0) +
        (results.userVO && Object.keys(results.userVO).length > 0 ? 1 : 0);

    // Display search stats
    const statsDiv = document.createElement('div');
    statsDiv.className = 'mb-4 text-gray-600';
    statsDiv.textContent = `共找到 ${totalCount} 个结果`;
    container.appendChild(statsDiv);

    // Display knowledge base results if available
    if (results.knowledgeBaseVO && Object.keys(results.knowledgeBaseVO).length > 0) {
        const kbSection = document.createElement('div');
        kbSection.className = 'mb-6';
        kbSection.innerHTML = `<h3 class="text-lg font-bold mb-2">知识库 (1)</h3>`;

        const kbGrid = document.createElement('div');
        kbGrid.className = 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4';

        // Create a knowledge base card
        const kb = results.knowledgeBaseVO;
        const kbCard = document.createElement('div');
        kbCard.className = 'knowledge-card bg-white shadow-md rounded p-4';
        kbCard.onclick = () => showKnowledgeDetail(kb.id);

        // Get a random image for the knowledge base
        const coverUrl = kb.coverUrl || `https://picsum.photos/seed/${kb.id || Math.random()}/200/150`;

        // Set card content
        kbCard.innerHTML = `
            <img src="${coverUrl}" alt="知识库封面" class="rounded">
            <h3 class="text-lg font-bold mt-2">${kb.name || '未命名知识库'}</h3>
            <p class="text-gray-600">${kb.description || '暂无描述'}</p>
            <span class="badge public bg-green-500 text-white px-2 py-1 rounded mt-2 inline-block">公开</span>
        `;

        kbGrid.appendChild(kbCard);
        kbSection.appendChild(kbGrid);
        container.appendChild(kbSection);
    }

    // Display article results if available
    if (results.articleVO && Object.keys(results.articleVO).length > 0) {
        const articleSection = document.createElement('div');
        articleSection.className = 'mb-6';
        articleSection.innerHTML = `<h3 class="text-lg font-bold mb-2">文章 (1)</h3>`;

        // Create article item
        const article = results.articleVO;
        const articleItem = document.createElement('div');
        articleItem.className = 'article-item p-2 hover:bg-gray-100 rounded cursor-pointer';
        articleItem.onclick = () => showArticleDetail(article.id);

        // Format time
        const createTime = article.createTime ? new Date(article.createTime).toLocaleString() : '未知日期';

        // Set article content
        articleItem.innerHTML = `
            <h4 class="text-md font-bold">${article.title || '未命名文章'}</h4>
            <p class="text-gray-600 text-sm">作者：${article.authorName || '未知'} | 发表时间：${createTime}</p>
            <p class="text-gray-600 text-sm">点赞：${article.likeCount || 0} | 评论：${article.commentCount || 0}</p>
        `;

        articleSection.appendChild(articleItem);
        container.appendChild(articleSection);
    }

    // Display user results if available
    if (results.userVO && Object.keys(results.userVO).length > 0) {
        const userSection = document.createElement('div');
        userSection.className = 'mb-6';
        userSection.innerHTML = `<h3 class="text-lg font-bold mb-2">用户 (1)</h3>`;

        const userGrid = document.createElement('div');
        userGrid.className = 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4';

        // Create user card
        const user = results.userVO;
        const userCard = document.createElement('div');
        userCard.className = 'user-card bg-white shadow-md rounded p-4 flex items-center';
        userCard.onclick = () => showUserProfile(user.id);

        // Set user card content
        userCard.innerHTML = `
            <img src="${user.avatar || 'https://picsum.photos/30/30'}" alt="用户头像" class="rounded-full w-12 h-12 mr-4">
            <div class="flex-1">
                <h4 class="text-md font-bold">${user.username || '未知用户'}</h4>
            </div>
            <button class="follow-btn bg-blue-500 text-white px-2 py-1 rounded text-sm" onclick="followUser(event, ${user.id})">关注</button>
        `;

        userGrid.appendChild(userCard);
        userSection.appendChild(userGrid);
        container.appendChild(userSection);
    }
}

// 渲染搜索结果
function renderSearchResults(data) {
    // 获取搜索结果容器
    const container = document.getElementById('search-results-container');
    container.innerHTML = '';

    // 创建标题
    const title = document.createElement('h2');
    title.className = 'text-xl font-bold mb-4';
    title.textContent = '搜索结果';
    container.appendChild(title);

    // 如果没有结果，显示提示
    if (data.total === 0) {
        const noResults = document.createElement('div');
        noResults.className = 'text-center text-gray-500 p-4';
        noResults.textContent = '没有找到相关内容';
        container.appendChild(noResults);
        return;
    }

    // 显示搜索统计
    const stats = document.createElement('p');
    stats.className = 'text-gray-600 mb-4';
    stats.textContent = `共找到 ${data.total} 个结果`;
    container.appendChild(stats);

    // 创建选项卡
    const tabs = document.createElement('div');
    tabs.className = 'flex space-x-4 mb-4';
    tabs.innerHTML = `
    <a href="#" class="tab-link active" data-tab="all">全部</a>
    <a href="#" class="tab-link" data-tab="knowledgeBases">知识库</a>
    <a href="#" class="tab-link" data-tab="articles">文章</a>
    <a href="#" class="tab-link" data-tab="users">用户</a>
  `;
    container.appendChild(tabs);

    // 绑定选项卡事件
    tabs.querySelectorAll('.tab-link').forEach(tab => {
        tab.addEventListener('click', function(e) {
            e.preventDefault();

            // 切换选项卡样式
            tabs.querySelectorAll('.tab-link').forEach(t => t.classList.remove('active'));
            this.classList.add('active');

            // 显示对应的结果
            const tabId = this.dataset.tab;
            document.querySelectorAll('.search-result-section').forEach(section => {
                if (tabId === 'all' || section.id === `${tabId}-results`) {
                    section.classList.remove('hidden');
                } else {
                    section.classList.add('hidden');
                }
            });
        });
    });

    // 创建知识库结果区域
    if (data.knowledgeBases && data.knowledgeBases.length > 0) {
        const kbSection = document.createElement('div');
        kbSection.className = 'search-result-section mb-8';
        kbSection.id = 'knowledgeBases-results';

        const kbTitle = document.createElement('h3');
        kbTitle.className = 'text-lg font-bold mb-2';
        kbTitle.textContent = '知识库';
        kbSection.appendChild(kbTitle);

        const kbGrid = document.createElement('div');
        kbGrid.className = 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4';

        data.knowledgeBases.forEach(kb => {
            kbGrid.appendChild(createKnowledgeBaseCard(kb));
        });

        kbSection.appendChild(kbGrid);
        container.appendChild(kbSection);
    }

    // 创建文章结果区域
    if (data.articles && data.articles.length > 0) {
        const articleSection = document.createElement('div');
        articleSection.className = 'search-result-section mb-8';
        articleSection.id = 'articles-results';

        const articleTitle = document.createElement('h3');
        articleTitle.className = 'text-lg font-bold mb-2';
        articleTitle.textContent = '文章';
        articleSection.appendChild(articleTitle);

        data.articles.forEach(article => {
            articleSection.appendChild(createArticleItem(article));
        });

        container.appendChild(articleSection);
    }

    // 创建用户结果区域
    if (data.users && data.users.length > 0) {
        const userSection = document.createElement('div');
        userSection.className = 'search-result-section mb-8';
        userSection.id = 'users-results';

        const userTitle = document.createElement('h3');
        userTitle.className = 'text-lg font-bold mb-2';
        userTitle.textContent = '用户';
        userSection.appendChild(userTitle);

        const userList = document.createElement('div');
        userList.className = 'grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4';

        data.users.forEach(user => {
            userList.appendChild(createUserCard(user));
        });

        userSection.appendChild(userList);
        container.appendChild(userSection);
    }
}