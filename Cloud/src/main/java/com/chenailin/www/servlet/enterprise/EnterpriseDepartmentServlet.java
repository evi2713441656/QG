package com.chenailin.www.servlet.enterprise;

import com.chenailin.www.core.container.BeanContainer;
import com.chenailin.www.model.dto.EnterpriseDepartmentDTO;
import com.chenailin.www.model.pojo.EnterpriseDepartment;
import com.chenailin.www.model.vo.EnterpriseDepartmentVO;
import com.chenailin.www.model.vo.ResultVO;
import com.chenailin.www.service.EnterpriseDepartmentService;
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
 * Servlet for enterprise department operations
 * @author evi
 */
@WebServlet("/enterprise/department/*")
public class EnterpriseDepartmentServlet extends HttpServlet {
    private EnterpriseDepartmentService departmentService;

    @Override
    public void init() throws ServletException {
        super.init();
        BeanContainer container = BeanContainer.getInstance();
        this.departmentService = (EnterpriseDepartmentService) container.getBean("enterpriseDepartmentService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getRequestURI().substring(req.getContextPath().length());
        Long userId = (Long) req.getSession().getAttribute("userId");

        try {
            if (path.startsWith("/enterprise/department/list/")) {
                Long enterpriseId = Long.parseLong(path.substring("/enterprise/department/list/".length()));
                List<EnterpriseDepartmentVO> departments = departmentService.listDepartments(enterpriseId, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(departments)));
            } else if (path.startsWith("/enterprise/department/managed")) {
                List<EnterpriseDepartmentVO> departments = departmentService.listManagedDepartments(userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(departments)));
            } else if (path.startsWith("/enterprise/department/")) {
                Long departmentId = Long.parseLong(path.substring("/enterprise/department/".length()));
                EnterpriseDepartmentVO department = departmentService.getDepartmentById(departmentId, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(department)));
            } else {
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
            if ("/enterprise/department".equals(path)) {
                EnterpriseDepartmentDTO dto = JsonUtil.fromJson(IOUtils.toString(req.getReader()), EnterpriseDepartmentDTO.class);
                EnterpriseDepartment department = departmentService.createDepartment(dto, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(department)));
            } else {
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
            if (path.startsWith("/enterprise/department/")) {
                // Update department
                Long departmentId = Long.parseLong(path.substring("/enterprise/department/".length()));
                EnterpriseDepartmentDTO dto = JsonUtil.fromJson(IOUtils.toString(req.getReader()), EnterpriseDepartmentDTO.class);
                dto.setId(departmentId);
                EnterpriseDepartment department = departmentService.updateDepartment(dto, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(department)));
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
            if (path.startsWith("/enterprise/department/")) {
                // Delete department
                Long departmentId = Long.parseLong(path.substring("/enterprise/department/".length()));
                departmentService.deleteDepartment(departmentId, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Department deleted successfully")));
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