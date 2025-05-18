package com.chenailin.www.servlet.knowledge;

import com.chenailin.www.model.dto.KnowledgeBaseDTO;
import com.chenailin.www.model.pojo.KnowledgeBase;
import com.chenailin.www.model.vo.KnowledgeBaseVO;
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
 * 处理知识库相关请求
 * @author evi
 */
@WebServlet("/knowledge/*")
public class KnowledgeBaseServlet extends BaseKnowledgeServlet {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeBaseServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = getRequestPath(req);
        Long userId = getUserId(req);

        try {
            if ("/knowledge/list/my".equals(path)) {
                // 获取用户的知识库
                if (!checkAuthentication(req, resp)) {
                    return;
                }
                List<KnowledgeBaseVO> list = knowledgeService.listUserBases(userId);
                sendJsonResponse(resp, ResultVO.success(list));
            } else if ("/knowledge/list/public".equals(path)) {
                // 获取公开知识库
                List<KnowledgeBaseVO> list = knowledgeService.listPublicBases();
                sendJsonResponse(resp, ResultVO.success(list));
            } else if (path.startsWith("/knowledge/")) {
                // 获取特定知识库
                Long id = Long.parseLong(path.substring("/knowledge/".length()));
                System.out.println(id);
                KnowledgeBaseVO vo = knowledgeService.getBaseById(id, userId);
                sendJsonResponse(resp, ResultVO.success(vo));
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的ID格式");
        } catch (Exception e) {
            logger.error("处理获取知识库请求失败", e);
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
            // 创建知识库
            KnowledgeBaseDTO dto = parseRequestBody(req, KnowledgeBaseDTO.class);
            KnowledgeBase base = knowledgeService.createBase(dto, userId);
            sendJsonResponse(resp, ResultVO.success(base));
        } catch (Exception e) {
            logger.error("处理创建知识库请求失败", e);
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
            if (path.startsWith("/knowledge/")) {
                // 更新知识库
                Long id = Long.parseLong(path.substring("/knowledge/".length()));
                KnowledgeBaseDTO dto = parseRequestBody(req, KnowledgeBaseDTO.class);
                KnowledgeBase updatedBase = knowledgeService.updateBase(id, dto, userId);
                sendJsonResponse(resp, ResultVO.success(updatedBase));
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的ID格式");
        } catch (Exception e) {
            logger.error("处理更新知识库请求失败", e);
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
            if (path.startsWith("/knowledge/")) {
                // 删除知识库
                Long id = Long.parseLong(path.substring("/knowledge/".length()));
                knowledgeService.deleteBase(id, userId);
                sendJsonResponse(resp, ResultVO.success("知识库删除成功"));
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的ID格式");
        } catch (Exception e) {
            logger.error("处理删除知识库请求失败", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}