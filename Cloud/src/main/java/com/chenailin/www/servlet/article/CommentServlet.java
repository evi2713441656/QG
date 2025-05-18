package com.chenailin.www.servlet.article;

import com.chenailin.www.model.dto.CommentDTO;
import com.chenailin.www.model.pojo.Comment;
import com.chenailin.www.model.vo.CommentVO;
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
 * 处理评论相关请求
 * @author evi
 */
@WebServlet("/comment/*")
public class CommentServlet extends BaseArticleServlet {
    private static final Logger logger = LoggerFactory.getLogger(CommentServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = getRequestPath(req);
        Long userId = getUserId(req);

        try {
            if (path.startsWith("/comment/article/")) {
                // 获取文章的评论
                Long articleId = Long.parseLong(path.substring("/comment/article/".length()));
                List<CommentVO> comments = commentService.getCommentsByArticle(articleId);
                sendJsonResponse(resp, ResultVO.success(comments));
            } else if (path.startsWith("/comment/user/")) {
                // 获取用户的评论
                Long targetUserId = Long.parseLong(path.substring("/comment/user/".length()));

                // 只允许查看自己的评论
                if (userId != null && userId.equals(targetUserId)) {
                    List<CommentVO> comments = commentService.getCommentsByUser(targetUserId);
                    sendJsonResponse(resp, ResultVO.success(comments));
                } else {
                    sendError(resp, HttpServletResponse.SC_FORBIDDEN, "无权访问");
                }
            } else if (path.startsWith("/comment/")) {
                // 获取特定评论
                Long commentId = Long.parseLong(path.substring("/comment/".length()));
                CommentVO comment = commentService.getCommentById(commentId);
                sendJsonResponse(resp, ResultVO.success(comment));
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的ID格式");
        } catch (Exception e) {
            logger.error("处理获取评论请求失败", e);
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

        try {
            // 创建新评论
            CommentDTO dto = parseRequestBody(req, CommentDTO.class);
            Comment comment = commentService.addComment(dto, userId);
            sendJsonResponse(resp, ResultVO.success(comment));
        } catch (Exception e) {
            logger.error("处理添加评论请求失败", e);
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
            if (path.startsWith("/comment/")) {
                // 更新评论
                Long commentId = Long.parseLong(path.substring("/comment/".length()));
                CommentDTO dto = parseRequestBody(req, CommentDTO.class);
                Comment updatedComment = commentService.updateComment(commentId, dto, userId);
                sendJsonResponse(resp, ResultVO.success(updatedComment));
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的ID格式");
        } catch (Exception e) {
            logger.error("处理更新评论请求失败", e);
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
            if (path.startsWith("/comment/")) {
                // 删除评论
                Long commentId = Long.parseLong(path.substring("/comment/".length()));
                commentService.deleteComment(commentId, userId);
                sendJsonResponse(resp, ResultVO.success("评论删除成功"));
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的ID格式");
        } catch (Exception e) {
            logger.error("处理删除评论请求失败", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}