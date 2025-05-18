package com.chenailin.www.servlet.user;

import com.chenailin.www.model.vo.BrowseHistoryVO;
import com.chenailin.www.model.vo.ResultVO;
import com.chenailin.www.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author evi
 */
@WebServlet("/browse-history/*")
public class BrowseHistoryServlet extends BaseUserServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Long userId = (Long) req.getSession().getAttribute("userId");

        try {
            int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 1;
            int size = req.getParameter("size") != null ? Integer.parseInt(req.getParameter("size")) : 10;

            List<BrowseHistoryVO> history = browseHistoryService.getUserBrowseHistory(userId, page, size);

            resp.getWriter().write(JsonUtil.toJson(ResultVO.success(history)));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error(400, "Invalid page or size parameter")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error(500, e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Long userId = (Long) req.getSession().getAttribute("userId");
        String path = req.getRequestURI().substring(req.getContextPath().length());

        try {
            if (path.startsWith("/browse-history/record")) {
                Long articleId = Long.parseLong(path.substring("/browse-history/record/".length()));
                browseHistoryService.recordBrowseHistory(userId, articleId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Browse history recorded successfully")));
            } else if ("/browse-history/clear".equals(path)) {
                browseHistoryService.clearUserBrowseHistory(userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Browse history cleared successfully")));
            } else {
                // Invalid path
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.error(400, "Invalid request path")));
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error(400, "Invalid ID format")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error(500, e.getMessage())));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Set response content type
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getRequestURI().substring(req.getContextPath().length());
        Long userId = (Long) req.getSession().getAttribute("userId");

        try {
            if (path.startsWith("/browse-history/")) {
                // Delete a specific browse history entry
                Long historyId = Long.parseLong(path.substring("/browse-history/".length()));
                browseHistoryService.deleteBrowseHistory(historyId, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Browse history entry deleted successfully")));
            } else {
                // Invalid path
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.error(400, "Invalid request path")));
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error(400, "Invalid ID format")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error(500, e.getMessage())));
        }
    }
}