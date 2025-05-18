package com.chenailin.www.servlet.article;

import com.chenailin.www.model.dto.ArticleDTO;
import com.chenailin.www.model.pojo.Article;
import com.chenailin.www.model.vo.ArticleVO;
import com.chenailin.www.model.vo.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 处理文章相关请求
 * @author evi
 */
@WebServlet("/article/*")
public class ArticleServlet extends BaseArticleServlet {
    private static final Logger logger = LoggerFactory.getLogger(ArticleServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = getRequestPath(req);
        Long userId = getUserId(req);

        try {
            if (path.startsWith("/article/list/")) {
                Long knowledgeBaseId = Long.parseLong(path.substring("/article/list/".length()));

                int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 1;
                int size = req.getParameter("size") != null ? Integer.parseInt(req.getParameter("size")) : 10;

                List<ArticleVO> articles = articleService.listArticlesByKnowledgeBase(knowledgeBaseId, userId, page, size);
                sendJsonResponse(resp, ResultVO.success(articles));
            } else if ("/article/recent".equals(path)) {
                int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 1;
                int size = req.getParameter("size") != null ? Integer.parseInt(req.getParameter("size")) : 10;
                String sortBy = req.getParameter("sortBy");

                List<ArticleVO> articles;
                if ("likes".equals(sortBy)) {
                    articles = articleService.getHottestArticles(page, size);
                } else {
                    articles = articleService.getLatestArticles(page, size);
                }

                sendJsonResponse(resp, ResultVO.success(articles));
            } else if (path.startsWith("/article/")) {
                Long articleId = Long.parseLong(path.substring("/article/".length()));
                ArticleVO article = articleService.getArticleById(articleId, userId);
                sendJsonResponse(resp, ResultVO.success(article));
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的ID格式");
        } catch (Exception e) {
            logger.error("处理获取文章请求失败", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Long userId = getUserId(req);

        // 检查用户是否已登录
        if (!checkAuthentication(req, resp)) {
            return;
        }

        String path = getRequestPath(req);

        try {
            if ("/article".equals(path)) {
                // 创建新文章
                ArticleDTO dto = parseRequestBody(req, ArticleDTO.class);
                Article article = articleService.createArticle(dto, userId);
                sendJsonResponse(resp, ResultVO.success(article));
            } else if (path.startsWith("/article/like/")) {
                // 点赞文章
                Long articleId = Long.parseLong(path.substring("/article/like/".length()));
                articleService.likeArticle(articleId, userId);
                sendJsonResponse(resp, ResultVO.success("文章点赞成功"));
            } else if (path.startsWith("/article/unlike/")) {
                // 取消点赞
                Long articleId = Long.parseLong(path.substring("/article/unlike/".length()));
                articleService.unlikeArticle(articleId, userId);
                sendJsonResponse(resp, ResultVO.success("取消点赞成功"));
            } else if (path.startsWith("/article/favorite/")) {
                // 收藏文章
                Long articleId = Long.parseLong(path.substring("/article/favorite/".length()));
                articleService.favoriteArticle(articleId, userId);
                sendJsonResponse(resp, ResultVO.success("文章收藏成功"));
            } else if (path.startsWith("/article/unfavorite/")) {
                // 取消收藏
                Long articleId = Long.parseLong(path.substring("/article/unfavorite/".length()));
                articleService.unfavoriteArticle(articleId, userId);
                sendJsonResponse(resp, ResultVO.success("取消收藏成功"));
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的ID格式");
        } catch (Exception e) {
            logger.error("处理文章操作请求失败", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = getRequestPath(req);
        Long userId = getUserId(req);

        // 检查用户是否已登录
        if (!checkAuthentication(req, resp)) {
            return;
        }

        try {
            if (path.startsWith("/article/")) {
                // 更新文章
                ArticleDTO dto = parseRequestBody(req, ArticleDTO.class);
                Article updatedArticle = articleService.updateArticle(dto, userId);
                sendJsonResponse(resp, ResultVO.success(updatedArticle));
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的ID格式");
        } catch (Exception e) {
            logger.error("处理更新文章请求失败", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = getRequestPath(req);
        Long userId = getUserId(req);

        // 检查用户是否已登录
        if (!checkAuthentication(req, resp)) {
            return;
        }

        try {
            if (path.startsWith("/article/")) {
                // 删除文章
                Long articleId = Long.parseLong(path.substring("/article/".length()));
                articleService.deleteArticle(articleId, userId);
                sendJsonResponse(resp, ResultVO.success("文章删除成功"));
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的ID格式");
        } catch (Exception e) {
            logger.error("处理删除文章请求失败", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}