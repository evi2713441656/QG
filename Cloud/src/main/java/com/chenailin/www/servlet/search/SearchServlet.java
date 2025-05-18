package com.chenailin.www.servlet.search;

import com.chenailin.www.core.container.BeanContainer;
import com.chenailin.www.model.vo.ResultVO;
import com.chenailin.www.model.vo.SearchResultVO;
import com.chenailin.www.service.SearchService;
import com.chenailin.www.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author evi
 */
@WebServlet("/search/*")
public class SearchServlet extends HttpServlet {
    protected SearchService searchService;

    @Override
    public void init() throws ServletException {
        super.init();
        BeanContainer container = BeanContainer.getInstance();
        this.searchService = (SearchService) container.getBean("searchService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getRequestURI().substring(req.getContextPath().length());
        Long userId = (Long) req.getSession().getAttribute("userId");
        String keyword = req.getParameter("keyword");

        if (keyword == null || keyword.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error(400, "Keyword is required")));
            return;
        }

        try {
            SearchResultVO result = null;

            switch (path) {
                case "/search/global":
                    result = searchService.search(keyword);
                    break;
                case "/search/knowledge":
                    result = new SearchResultVO();
                    result.setKnowledgeBaseVO(searchService.searchKnowledgeBase(keyword));
                    if (result.getKnowledgeBaseVO() == null) {
                        System.out.println("null111");
                    }

                    result.setTotal(result.getKnowledgeBaseVO() != null ? 1 : 0);
                    break;
                case "/search/article":
                    result = new SearchResultVO();
                    result.setArticleVO(searchService.searchArticle(keyword));
                    result.setTotal(result.getArticleVO() != null ? 1 : 0);
                    break;
                case "/search/user":
                    result = new SearchResultVO();
                    result.setUserVO(searchService.searchUser(keyword));
                    result.setTotal(result.getUserVO() != null ? 1 : 0);
                    break;
                case "/search/enterprise":
                    System.out.println("Enterprise search not implemented yet");
                    resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
                    resp.getWriter().write(JsonUtil.toJson(ResultVO.error(501, "Enterprise search not implemented yet")));
                    return;
                default:
                    // Invalid path
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(JsonUtil.toJson(ResultVO.error(400, "Invalid search type")));
                    return;
            }

            // Ensure we have a result object
            if (result == null) {
                result = new SearchResultVO();
                result.setTotal(0);
            }

            // Return the result
            resp.getWriter().write(JsonUtil.toJson(ResultVO.success(result)));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error(400, "Invalid page or size parameter")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error(500, e.getMessage())));
        }
    }
}