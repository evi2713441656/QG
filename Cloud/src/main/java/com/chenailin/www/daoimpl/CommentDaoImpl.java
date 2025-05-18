package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.CommentDao;
import com.chenailin.www.exception.DataAccessException;
import com.chenailin.www.model.pojo.Comment;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author evi
 */
public class CommentDaoImpl extends BaseDao<Comment> implements CommentDao {

    @Override
    protected Comment mapResultSet(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setContent(rs.getString("content"));
        comment.setArticleId(rs.getLong("article_id"));
        comment.setUserId(rs.getLong("user_id"));

        // 处理可能为空的parent_id
        long parentId = rs.getLong("parent_id");
        if (!rs.wasNull()) {
            comment.setParentId(parentId);
        }

        comment.setCreateTime(rs.getTimestamp("create_time"));
        return comment;
    }

    @Override
    public Comment findById(Long id) {
        String sql = "SELECT * FROM comment WHERE id = ?";
        return executeQuerySingle(sql, id);
    }

    @Override
    public List<Comment> findByArticleId(Long articleId) {
        String sql = "SELECT * FROM comment WHERE article_id = ? ORDER BY create_time ASC";
        return executeQuery(sql, articleId);
    }

    @Override
    public List<Comment> findByUserId(Long userId) {
        String sql = "SELECT * FROM comment WHERE user_id = ? ORDER BY create_time DESC";
        return executeQuery(sql, userId);
    }

    @Override
    public List<Comment> findReplies(Long parentId) {
        String sql = "SELECT * FROM comment WHERE parent_id = ? ORDER BY create_time ASC";
        return executeQuery(sql, parentId);
    }

    @Override
    public void save(Comment comment) {
        String sql = "INSERT INTO comment (content, article_id, user_id, parent_id, create_time) VALUES (?, ?, ?, ?, ?)";
        Object[] params;
        if (comment.getParentId() != null) {
            params = new Object[]{comment.getContent(), comment.getArticleId(), comment.getUserId(), comment.getParentId(),
                    new java.sql.Timestamp(comment.getCreateTime().getTime())};
        } else {
            params = new Object[]{comment.getContent(), comment.getArticleId(), comment.getUserId(), null,
                    new java.sql.Timestamp(comment.getCreateTime().getTime())};
        }
        Long id = executeInsertWithGeneratedKey(sql, params);
        if (id == null) {
            throw new DataAccessException("创建评论失败，无法获取ID");
        }
        comment.setId(id);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM comment WHERE id = ?";
        executeUpdate(sql, id);
    }

    @Override
    public void deleteByArticleId(Long articleId) {
        String sql = "DELETE FROM comment WHERE article_id = ?";
        executeUpdate(sql, articleId);
    }

    @Override
    public int countByArticleId(Long articleId) {
        String sql = "SELECT COUNT(*) FROM comment WHERE article_id = ?";
        return executeCountQuery(sql, articleId);
    }
}