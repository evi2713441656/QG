package com.chenailin.www.servlet.enterprise;

import com.chenailin.www.core.container.BeanContainer;
import com.chenailin.www.model.dto.EnterpriseAttendanceDTO;
import com.chenailin.www.model.vo.EnterpriseAttendanceVO;
import com.chenailin.www.model.vo.ResultVO;
import com.chenailin.www.service.EnterpriseAttendanceService;
import com.chenailin.www.util.JsonUtil;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Servlet for enterprise attendance operations
 * @author evi
 */
@WebServlet("/enterprise/attendance/*")
public class EnterpriseAttendanceServlet extends HttpServlet {
    private EnterpriseAttendanceService attendanceService;

    @Override
    public void init() throws ServletException {
        super.init();
        BeanContainer container = BeanContainer.getInstance();
        this.attendanceService = (EnterpriseAttendanceService) container.getBean("enterpriseAttendanceService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getRequestURI().substring(req.getContextPath().length());
        Long userId = (Long) req.getSession().getAttribute("userId");

        try {
            if (path.startsWith("/enterprise/attendance/list/")) {
                // Get attendance records for an enterprise
                Long enterpriseId = Long.parseLong(path.substring("/enterprise/attendance/list/".length()));

                // Parse query parameters for filtering
                String startDateStr = req.getParameter("startDate");
                String endDateStr = req.getParameter("endDate");
                String departmentIdStr = req.getParameter("departmentId");
                String pageStr = req.getParameter("page");
                String sizeStr = req.getParameter("size");

                // Default values and parsing
                Date startDate = startDateStr != null ? new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr) : null;
                Date endDate = endDateStr != null ? new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr) : null;
                Long departmentId = departmentIdStr != null && !departmentIdStr.isEmpty() ? Long.parseLong(departmentIdStr) : null;
                int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
                int size = sizeStr != null ? Integer.parseInt(sizeStr) : 10;

                // Get attendance records
                List<EnterpriseAttendanceVO> records = attendanceService.listAttendanceRecords(
                        enterpriseId, userId, startDate, endDate, departmentId, page, size);

                // Get total count for pagination
                int total = attendanceService.countAttendanceRecords(enterpriseId, userId, startDate, endDate, departmentId);

                // Create result with pagination info
                //111
//                resp.getWriter().write(JsonUtil.toJson(ResultVO.successWithPagination(records, page, size, total)));
            } else if (path.startsWith("/enterprise/attendance/status/")) {
                // Get current attendance status
                Long enterpriseId = Long.parseLong(path.substring("/enterprise/attendance/status/".length()));
                EnterpriseAttendanceVO status = attendanceService.getUserAttendanceStatus(enterpriseId, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(status)));
            } else if (path.startsWith("/enterprise/attendance/my")) {
                // Get user's own attendance records
                String startDateStr = req.getParameter("startDate");
                String endDateStr = req.getParameter("endDate");
                String pageStr = req.getParameter("page");
                String sizeStr = req.getParameter("size");

                // Default values and parsing
                Date startDate = startDateStr != null ? new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr) : null;
                Date endDate = endDateStr != null ? new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr) : null;
                int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
                int size = sizeStr != null ? Integer.parseInt(sizeStr) : 10;

                // Get user's attendance records
                List<EnterpriseAttendanceVO> records = attendanceService.listUserAttendanceRecords(
                        userId, startDate, endDate, page, size);

                // Get total count for pagination
                int total = attendanceService.countUserAttendanceRecords(userId, startDate, endDate);

                // Create result with pagination info
                //111
//                resp.getWriter().write(JsonUtil.toJson(ResultVO.successWithPagination(records, page, size, total)));
            } else {
                // Invalid path
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.error(400, "Invalid request path")));
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error(400, "Invalid ID or parameter format")));
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
            switch (path) {
                case "/enterprise/attendance/clock-in": {
                    // Clock in
                    EnterpriseAttendanceDTO dto = JsonUtil.fromJson(IOUtils.toString(req.getReader()), EnterpriseAttendanceDTO.class);
                    EnterpriseAttendanceVO attendanceVO = attendanceService.clockIn(dto.getEnterpriseId(), userId);
                    resp.getWriter().write(JsonUtil.toJson(ResultVO.success(attendanceVO)));
                    break;
                }
                case "/enterprise/attendance/clock-out": {
                    // Clock out
                    EnterpriseAttendanceDTO dto = JsonUtil.fromJson(IOUtils.toString(req.getReader()), EnterpriseAttendanceDTO.class);
                    EnterpriseAttendanceVO attendanceVO = attendanceService.clockOut(dto.getEnterpriseId(), userId);
                    resp.getWriter().write(JsonUtil.toJson(ResultVO.success(attendanceVO)));
                    break;
                }
                case "/enterprise/attendance/admin-record": {
                    // Admin recording attendance for user (manual addition)
                    EnterpriseAttendanceDTO dto = JsonUtil.fromJson(IOUtils.toString(req.getReader()), EnterpriseAttendanceDTO.class);
                    EnterpriseAttendanceVO attendanceVO = attendanceService.recordAttendance(dto, userId);
                    resp.getWriter().write(JsonUtil.toJson(ResultVO.success(attendanceVO)));
                    break;
                }
                default:
                    // Invalid path
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(JsonUtil.toJson(ResultVO.error(400, "Invalid request path")));
                    break;
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
            if (path.startsWith("/enterprise/attendance/")) {
                // Update attendance record (admin only)
                Long attendanceId = Long.parseLong(path.substring("/enterprise/attendance/".length()));
                EnterpriseAttendanceDTO dto = JsonUtil.fromJson(IOUtils.toString(req.getReader()), EnterpriseAttendanceDTO.class);
                dto.setId(attendanceId);
                EnterpriseAttendanceVO attendanceVO = attendanceService.updateAttendance(dto, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(attendanceVO)));
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
            if (path.startsWith("/enterprise/attendance/")) {
                // Delete attendance record (admin only)
                Long attendanceId = Long.parseLong(path.substring("/enterprise/attendance/".length()));
                attendanceService.deleteAttendance(attendanceId, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Attendance record deleted successfully")));
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