package com.chenailin.www.servlet;

import com.chenailin.www.core.container.BeanContainer;
import com.chenailin.www.model.dto.UserMessageDTO;
import com.chenailin.www.model.vo.ResultVO;
import com.chenailin.www.model.vo.UserMessageVO;
import com.chenailin.www.service.UserMessageService;
import com.chenailin.www.util.JsonUtil;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet for handling private messages between users
 * @author evi
 */
@WebServlet("/message/*")
public class MessageServlet extends HttpServlet {
    private UserMessageService messageService;

    @Override
    public void init() throws ServletException {
        super.init();
        BeanContainer container = BeanContainer.getInstance();
        this.messageService = (UserMessageService) container.getBean("userMessageService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getRequestURI().substring(req.getContextPath().length());
        Long userId = (Long) req.getSession().getAttribute("userId");

        try {
            if ("/message/unread-count".equals(path)) {
                // Get unread message count
                int count = messageService.countUnreadMessages(userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(count)));
            } else if ("/message/list".equals(path)) {
                // Get messages with pagination
                String typeParam = req.getParameter("type");
                String pageParam = req.getParameter("page");
                String sizeParam = req.getParameter("size");

                int page = pageParam != null ? Integer.parseInt(pageParam) : 1;
                int size = sizeParam != null ? Integer.parseInt(sizeParam) : 10;

                List<UserMessageVO> messages;
                int total;

                // If type is specified, filter by type
                if (typeParam != null && !typeParam.isEmpty()) {
                    messages = messageService.getMessagesByType(userId, typeParam, page, size);
                    total = messageService.countMessagesByType(userId, typeParam);
                } else {
                    messages = messageService.getAllMessages(userId, page, size);
                    total = messageService.countAllMessages(userId);
                }

                //111
//                resp.getWriter().write(JsonUtil.toJson(ResultVO.successWithPagination(messages, page, size, total)));
            } else if (path.startsWith("/message/conversation/")) {
                // Get conversation messages
                Long otherUserId = Long.parseLong(path.substring("/message/conversation/".length()));
                String pageParam = req.getParameter("page");
                String sizeParam = req.getParameter("size");

                int page = pageParam != null ? Integer.parseInt(pageParam) : 1;
                int size = sizeParam != null ? Integer.parseInt(sizeParam) : 20;

                List<UserMessageVO> messages = messageService.getConversation(userId, otherUserId, page, size);
                int total = messageService.countConversationMessages(userId, otherUserId);

                //111
//                resp.getWriter().write(JsonUtil.toJson(ResultVO.successWithPagination(messages, page, size, total)));
            } else if (path.startsWith("/message/new/")) {
                // Get new messages since last message ID
                Long otherUserId = Long.parseLong(path.substring("/message/new/".length()));
                String lastIdParam = req.getParameter("lastId");
                Long lastId = lastIdParam != null ? Long.parseLong(lastIdParam) : 0L;

                List<UserMessageVO> newMessages = messageService.getNewConversationMessages(userId, otherUserId, lastId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(newMessages)));
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Long userId = (Long) req.getSession().getAttribute("userId");
        String path = req.getRequestURI().substring(req.getContextPath().length());

        try {
            if ("/message/send".equals(path)) {
                // Send message
                UserMessageDTO dto = JsonUtil.fromJson(IOUtils.toString(req.getReader()), UserMessageDTO.class);
                UserMessageVO message = messageService.sendMessage(userId, dto.getRecipientId(), dto.getContent());
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(message)));
            } else {
                // Invalid path
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.error(400, "Invalid request path")));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error(500, e.getMessage())));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Long userId = (Long) req.getSession().getAttribute("userId");
        String path = req.getRequestURI().substring(req.getContextPath().length());

        try {
            if (path.startsWith("/message/read/")) {
                // Mark message as read
                Long messageId = Long.parseLong(path.substring("/message/read/".length()));
                messageService.markAsRead(messageId, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Message marked as read")));
            } else if ("/message/read-all".equals(path)) {
                // Mark all messages as read
                messageService.markAllAsRead(userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("All messages marked as read")));
            } else if (path.startsWith("/message/read-conversation/")) {
                // Mark all messages in a conversation as read
                Long otherUserId = Long.parseLong(path.substring("/message/read-conversation/".length()));
                messageService.markConversationAsRead(userId, otherUserId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Conversation marked as read")));
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
        resp.setContentType("application/json;charset=UTF-8");
        Long userId = (Long) req.getSession().getAttribute("userId");
        String path = req.getRequestURI().substring(req.getContextPath().length());

        try {
            if (path.startsWith("/message/")) {
                // Delete message
                Long messageId = Long.parseLong(path.substring("/message/".length()));
                messageService.deleteMessage(messageId, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Message deleted")));
            } else if (path.startsWith("/message/conversation/")) {
                // Delete conversation
                Long otherUserId = Long.parseLong(path.substring("/message/conversation/".length()));
                messageService.deleteConversation(userId, otherUserId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Conversation deleted")));
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