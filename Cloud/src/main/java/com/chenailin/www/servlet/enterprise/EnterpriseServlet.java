package com.chenailin.www.servlet.enterprise;

import com.chenailin.www.core.container.BeanContainer;
import com.chenailin.www.model.dto.EnterpriseDTO;
import com.chenailin.www.model.dto.EnterpriseMemberDTO;
import com.chenailin.www.model.pojo.Enterprise;
import com.chenailin.www.model.vo.EnterpriseVO;
import com.chenailin.www.model.vo.ResultVO;
import com.chenailin.www.service.EnterpriseService;
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
 * @author evi
 */
@WebServlet("/enterprise/*")
public class EnterpriseServlet extends HttpServlet {
    protected EnterpriseService enterpriseService;

    @Override
    public void init() throws ServletException {
        super.init();
        BeanContainer container = BeanContainer.getInstance();
        this.enterpriseService = (EnterpriseService) container.getBean("enterpriseService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getRequestURI().substring(req.getContextPath().length());
        Long userId = (Long) req.getSession().getAttribute("userId");

        try {
            if ("/enterprise/list".equals(path)) {
                System.out.println("111");
                // Get enterprises that the user is a member of
                List<EnterpriseVO> enterprises = enterpriseService.getUserEnterprises(userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(enterprises)));
            } else if ("/enterprise/owned".equals(path)) {
                // Get enterprises owned by the user
                List<EnterpriseVO> enterprises = enterpriseService.getOwnedEnterprises(userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(enterprises)));
            } else if (path.startsWith("/enterprise/members/")) {
                // Get members of an enterprise
                Long enterpriseId = Long.parseLong(path.substring("/enterprise/members/".length()));
//                List<EnterpriseMember> members = enterpriseService.getEnterpriseMembers(enterpriseId, userId);
//                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(members)));
                System.out.println("尚未实现此功能");
            } else if (path.startsWith("/enterprise/")) {
                // Get enterprise by ID
                Long enterpriseId = Long.parseLong(path.substring("/enterprise/".length()));
                EnterpriseVO enterprise = enterpriseService.getEnterpriseById(enterpriseId, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(enterprise)));
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
        // Set response content type
        resp.setContentType("application/json;charset=UTF-8");
        Long userId = (Long) req.getSession().getAttribute("userId");
        String path = req.getRequestURI().substring(req.getContextPath().length());

        try {
            if ("/enterprise".equals(path)) {
                // Create new enterprise
                EnterpriseDTO dto = JsonUtil.fromJson(IOUtils.toString(req.getReader()), EnterpriseDTO.class);
                Enterprise enterprise = enterpriseService.createEnterprise(dto, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(enterprise)));
            } else if (path.startsWith("/enterprise/invite/")) {
                // Invite user to enterprise
                Long enterpriseId = Long.parseLong(path.substring("/enterprise/invite/".length()));
                EnterpriseMemberDTO dto = JsonUtil.fromJson(IOUtils.toString(req.getReader()), EnterpriseMemberDTO.class);
//                enterpriseService.inviteMember(enterpriseId, dto, userId);
                System.out.println("尚未实现此功能");
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Invitation sent successfully")));
            } else if (path.startsWith("/enterprise/join/")) {
                // Join enterprise with invitation code
                String inviteCode = path.substring("/enterprise/join/".length());
//                enterpriseService.joinEnterprise(inviteCode, userId);
                System.out.println("尚未实现此功能");
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Joined enterprise successfully")));
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
        // Set response content type
        resp.setContentType("application/json;charset=UTF-8");
        String path = req.getRequestURI().substring(req.getContextPath().length());
        Long userId = (Long) req.getSession().getAttribute("userId");

        try {
            if (path.startsWith("/enterprise/")) {
                Long enterpriseId = Long.parseLong(path.substring("/enterprise/".length()));
                EnterpriseDTO dto = JsonUtil.fromJson(IOUtils.toString(req.getReader()), EnterpriseDTO.class);
                Enterprise updatedEnterprise = enterpriseService.updateEnterprise(dto, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success(updatedEnterprise)));
            } else if (path.startsWith("/enterprise/member/")) {
                // Update member role
                Long memberId = Long.parseLong(path.substring("/enterprise/member/".length()));
                EnterpriseMemberDTO dto = JsonUtil.fromJson(IOUtils.toString(req.getReader()), EnterpriseMemberDTO.class);
//                enterpriseService.updateMemberRole(memberId, dto, userId);
                System.out.println("尚未实现此功能");
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Member role updated successfully")));
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
            if (path.startsWith("/enterprise/")) {
                // Delete enterprise
                Long enterpriseId = Long.parseLong(path.substring("/enterprise/".length()));
                enterpriseService.deleteEnterprise(enterpriseId, userId);
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Enterprise deleted successfully")));
            } else if (path.startsWith("/enterprise/leave/")) {
                // Leave enterprise
                Long enterpriseId = Long.parseLong(path.substring("/enterprise/leave/".length()));
//                enterpriseService.leaveEnterprise(enterpriseId, userId);
                System.out.println("尚未实现此功能");
                resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Left enterprise successfully")));
            } else if (path.startsWith("/enterprise/member/")) {
                // Remove member from enterprise
                String[] parts = path.substring("/enterprise/member/".length()).split("/");
                if (parts.length == 2) {
                    Long enterpriseId = Long.parseLong(parts[0]);
                    Long memberId = Long.parseLong(parts[1]);
//                    enterpriseService.removeMember(enterpriseId, memberId, userId);
                    System.out.println("尚未实现此功能");
                    resp.getWriter().write(JsonUtil.toJson(ResultVO.success("Member removed successfully")));
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write(JsonUtil.toJson(ResultVO.error(400, "Invalid request path")));
                }
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