package com.chenailin.www.servlet.user;

import com.chenailin.www.model.dto.AvatarDTO;
import com.chenailin.www.model.vo.ResultVO;
import com.chenailin.www.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * @author evi
 */
@WebServlet(urlPatterns = {"/modify/*"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 5,      // 5 MB
        maxRequestSize = 1024 * 1024 * 10   // 10 MB
)
public class ModifyServlet extends BaseUserServlet {
    private static final Logger logger = LoggerFactory.getLogger(ModifyServlet.class);

    // Windows 文件存储路径 - 请根据你的实际环境修改
    private static final String UPLOAD_DIR = "D:\\code\\cat\\avatar";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            if ("/modify/upload-avatar".equals(path)) {
                handleUploadAvatar(req, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write(JsonUtil.toJson(ResultVO.error("接口不存在")));
            }
        } catch (Exception e) {
            logger.error("处理请求出错", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write(JsonUtil.toJson(ResultVO.error("服务器内部错误: " + e.getMessage())));
        }
    }

    private void handleUploadAvatar(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        // 1. 确保上传目录存在（添加日志以便调试）
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            logger.info("上传目录创建结果: {}", created ? "成功" : "失败");
        }
        logger.info("上传目录路径: {}", uploadDir.getAbsolutePath());

        // 2. 获取上传的文件部分
        Part filePart = req.getPart("avatar");
        if (filePart == null || filePart.getSize() == 0) {
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error("请选择头像文件")));
            return;
        }

        // 3. 验证文件类型
        String contentType = filePart.getContentType();
        if (!contentType.startsWith("image/")) {
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error("只支持图片文件")));
            return;
        }

        // 4. 生成唯一文件名
        String originalFileName = getSubmittedFileName(filePart);
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + fileExtension;

        // 防止文件名以斜杠开头
        if (newFileName.startsWith("/") || newFileName.startsWith("\\")) {
            newFileName = newFileName.substring(1);
        }
        logger.info("生成的文件名: {}", newFileName);

        // 5. 保存文件（修正路径拼接方式）
        Path filePath = Paths.get(UPLOAD_DIR, newFileName);
        logger.info("最终保存路径: {}", filePath);

        try {
            Files.copy(filePart.getInputStream(), filePath);
            logger.info("文件保存成功: {}", filePath);

            // 6. 构造访问URL（根据你的实际访问方式调整）
            String fileUrl = "/avatar/" + newFileName;
            logger.info("保存到数据库的URL: {}", fileUrl);

            // 7. 更新数据库
            AvatarDTO avatarDTO = new AvatarDTO();
            avatarDTO.setAvatarUrl(fileUrl);
            avatarDTO.setEmail(getEmailFromRequest(req));

            userService.updateAvatar(avatarDTO);

            // 8. 返回成功响应
            resp.getWriter().write(JsonUtil.toJson(ResultVO.success("头像上传成功")));
        } catch (Exception e) {
            logger.error("保存头像文件失败", e);
            // 如果保存失败，尝试删除可能已部分写入的文件
            try {
                Files.deleteIfExists(filePath);
            } catch (Exception ex) {
                logger.error("删除失败文件出错", ex);
            }
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error("头像上传失败: " + e.getMessage())));
        }
    }

    // 示例方法，你需要根据你的认证系统实现
    private String getEmailFromRequest(HttpServletRequest req) {
        // 例如从session或token中获取用户ID
        return "2713441656@qq.com";
    }

    private String getSubmittedFileName(Part part) {
        return Stream.of(part.getHeader("content-disposition").split(";"))
                .map(String::trim)
                .filter(s -> s.startsWith("filename="))
                .map(s -> s.substring(s.indexOf('=') + 1).replace("\"", ""))
                .findFirst()
                .orElse(null);
    }
}