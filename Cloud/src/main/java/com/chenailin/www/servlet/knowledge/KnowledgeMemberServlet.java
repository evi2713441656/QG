package com.chenailin.www.servlet.knowledge;

import com.chenailin.www.model.dto.KnowledgeMemberDTO;
import com.chenailin.www.model.pojo.KnowledgeMember;
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
 * 处理知识库成员相关请求
 * @author evi
 */
@WebServlet("/knowledge/member/*")
public class KnowledgeMemberServlet extends BaseKnowledgeServlet {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeMemberServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = getRequestPath(req);
        Long userId = getUserId(req);

        // 检查用户是否已登录
        if (!checkAuthentication(req, resp)) {
            return;
        }

        try {
            if (path.startsWith("/knowledge/member/list/")) {
                // 获取知识库成员列表
                Long knowledgeId = Long.parseLong(path.substring("/knowledge/member/list/".length()));
                List<KnowledgeMember> members = knowledgeService.listMembers(knowledgeId, userId);
                sendJsonResponse(resp, ResultVO.success(members));
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的ID格式");
        } catch (Exception e) {
            logger.error("处理获取知识库成员请求失败", e);
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
            // 添加知识库成员
            KnowledgeMemberDTO dto = parseRequestBody(req, KnowledgeMemberDTO.class);
            knowledgeService.addMember(dto, userId);
            sendJsonResponse(resp, ResultVO.success("成员添加成功"));
        } catch (Exception e) {
            logger.error("处理添加知识库成员请求失败", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Long userId = getUserId(req);

        // 检查用户是否已登录
        if (!checkAuthentication(req, resp)) {
            return;
        }

        try {
            // 更新知识库成员角色
            KnowledgeMemberDTO dto = parseRequestBody(req, KnowledgeMemberDTO.class);
            knowledgeService.updateMemberRole(dto, userId);
            sendJsonResponse(resp, ResultVO.success("成员角色更新成功"));
        } catch (Exception e) {
            logger.error("处理更新知识库成员角色请求失败", e);
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
            if (path.startsWith("/knowledge/member/")) {
                // 删除知识库成员
                String[] parts = path.substring("/knowledge/member/".length()).split("/");
                if (parts.length == 2) {
                    Long knowledgeId = Long.parseLong(parts[0]);
                    Long memberId = Long.parseLong(parts[1]);
                    knowledgeService.removeMember(knowledgeId, memberId, userId);
                    sendJsonResponse(resp, ResultVO.success("成员移除成功"));
                } else {
                    sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
                }
            } else {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的请求路径");
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "无效的ID格式");
        } catch (Exception e) {
            logger.error("处理删除知识库成员请求失败", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}