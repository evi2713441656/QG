package com.chenailin.www.servlet;

import com.chenailin.www.core.container.BeanContainer;
import com.chenailin.www.model.vo.NotificationVO;
import com.chenailin.www.model.vo.ResultVO;
import com.chenailin.www.service.NotificationService;
import com.chenailin.www.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet for handling system notifications
 * @author evi
 */
@WebServlet("/notification/*")
public class NotificationServlet extends HttpServlet {
    private NotificationService notificationService;

    @Override
    public void init() throws ServletException {
        super.init();
        BeanContainer container = BeanContainer.getInstance();
        this.notificationService = (NotificationService) container.getBean("notificationService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getRequestURI().substring(req.getContextPath().length());
        Long userId = (Long) req.getSession().getAttribute("userId");

        try {
            if ("/notification/count".equals(path)) {
                // Get unread notification count
                int count = notificationService.countUnreadNotifications(userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(count)));
            } else if ("/notification/list".equals(path)) {
                // Get notifications with pagination
                String typeParam = req.getParameter("type");
                String pageParam = req.getParameter("page");
                String sizeParam = req.getParameter("size");

                int page = pageParam != null ? Integer.parseInt(pageParam) : 1;
                int size = sizeParam != null ? Integer.parseInt(sizeParam) : 10;

                List<NotificationVO> notifications;
                int total;

                // If type is specified, filter by type
                if (typeParam != null && !typeParam.isEmpty()) {
                    notifications = notificationService.getNotificationsByType(userId, typeParam, page, size);
                    total = notificationService.countNotificationsByType(userId, typeParam);
                } else {
                    notifications = notificationService.getAllNotifications(userId, page, size);
                    total = notificationService.countAllNotifications(userId);
                }

                //111
//                resp.getWriter().write(JsonUtil.toJson(ResultVO.successWithPagination(notifications, page, size, total)));
            } else if ("/notification/unread".equals(path)) {
                // Get unread notifications
                String pageParam = req.getParameter("page");
                String sizeParam = req.getParameter("size");

                int page = pageParam != null ? Integer.parseInt(pageParam) : 1;
                int size = sizeParam != null ? Integer.parseInt(sizeParam) : 10;

                List<NotificationVO> notifications = notificationService.getUnreadNotifications(userId, page, size);
                int total = notificationService.countUnreadNotifications(userId);
//111
//                resp.getWriter().write(JsonUtil.toJson(ResultVO.successWithPagination(notifications, page, size, total)));
            } else if (path.startsWith("/notification/")) {
                // Get notification by ID
                Long notificationId = Long.parseLong(path.substring("/notification/".length()));
                NotificationVO notification = notificationService.getNotificationById(notificationId, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(notification)));
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
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Long userId = (Long) req.getSession().getAttribute("userId");
        String path = req.getRequestURI().substring(req.getContextPath().length());

        try {
            if (path.startsWith("/notification/read/")) {
                // Mark notification as read
                Long notificationId = Long.parseLong(path.substring("/notification/read/".length()));
                notificationService.markAsRead(notificationId, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Notification marked as read")));
            } else if ("/notification/read-all".equals(path)) {
                // Mark all notifications as read
                notificationService.markAllAsRead(userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("All notifications marked as read")));
            } else if (path.startsWith("/notification/read-type/")) {
                // Mark all notifications of a specific type as read
                String type = path.substring("/notification/read-type/".length());
                notificationService.markAllAsReadByType(userId, type);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("All notifications of type " + type + " marked as read")));
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
            if (path.startsWith("/notification/")) {
                // Delete notification
                Long notificationId = Long.parseLong(path.substring("/notification/".length()));
                notificationService.deleteNotification(notificationId, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Notification deleted")));
            } else if ("/notification/clear-all".equals(path)) {
                // Delete all notifications
                notificationService.deleteAllNotifications(userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("All notifications deleted")));
            } else if ("/notification/clear-read".equals(path)) {
                // Delete all read notifications
                notificationService.deleteReadNotifications(userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("All read notifications deleted")));
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