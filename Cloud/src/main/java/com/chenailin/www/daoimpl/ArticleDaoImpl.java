package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.ArticleDao;
import com.chenailin.www.exception.DataAccessException;
import com.chenailin.www.model.pojo.Article;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author evi
 */
public class ArticleDaoImpl extends BaseDao<Article> implements ArticleDao {

    @Override
    protected Article mapResultSet(ResultSet rs) throws SQLException {
        Article article = new Article();
        article.setId(rs.getLong("id"));
        article.setTitle(rs.getString("title"));
        article.setContent(rs.getString("content"));
        article.setKnowledgeId(rs.getLong("knowledge_id"));
        article.setAuthorId(rs.getLong("author_id"));
        article.setViewCount(rs.getInt("view_count"));
        article.setLikeCount(rs.getInt("like_count"));
        article.setCommentCount(rs.getInt("comment_count"));
        article.setPinned(rs.getBoolean("is_pinned"));
        article.setCreateTime(rs.getTimestamp("create_time"));
        article.setUpdateTime(rs.getTimestamp("update_time"));
        return article;
    }

    @Override
    public Article findById(Long id) {
        String sql = "SELECT * FROM article WHERE id = ?";
        return executeQuerySingle(sql, id);
    }

    @Override
    public List<Article> findByKnowledgeIdWithPagination(Long knowledgeId, int limit, int offset) {
        String sql = "SELECT * FROM article WHERE knowledge_id = ? ORDER BY create_time DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, knowledgeId, limit, offset);
    }

    @Override
    public List<Article> findByKnowledgeId(Long knowledgeId) {
        String sql = "SELECT * FROM article WHERE knowledge_id = ? ORDER BY create_time DESC";
        return executeQuery(sql, knowledgeId);
    }

    @Override
    public List<Article> findLatest(int limit, int offset) {
        String sql = "SELECT * FROM article ORDER BY create_time DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, limit, offset);
    }

    @Override
    public List<Article> findHottest(int limit, int offset) {
        String sql = "SELECT * FROM article ORDER BY like_count DESC, view_count DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, limit, offset);
    }

    @Override
    public void save(Article article) {
        String sql = "INSERT INTO article (title, content, knowledge_id, author_id, view_count, like_count, " +
                "comment_count, is_pinned, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Long id = executeInsertWithGeneratedKey(sql, article.getTitle(), article.getContent(), article.getKnowledgeId(), article.getAuthorId(),
                article.getViewCount(), article.getLikeCount(), article.getCommentCount(), article.isPinned(),
                new java.sql.Timestamp(article.getCreateTime().getTime()), new java.sql.Timestamp(article.getUpdateTime().getTime()));
        if (id == null) {
            throw new DataAccessException("创建文章失败，无法获取ID");
        }
        article.setId(id);
    }

    @Override
    public void update(Article article) {
        String sql = "UPDATE article SET title = ?, content = ?, is_pinned = ?, update_time = ? WHERE id = ?";
        int affectedRows = executeUpdate(sql, article.getTitle(), article.getContent(), article.isPinned(),
                new java.sql.Timestamp(article.getUpdateTime().getTime()), article.getId());
        if (affectedRows == 0) {
            throw new DataAccessException("更新文章失败，没有行被影响");
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM article WHERE id = ?";
        int affectedRows = executeUpdate(sql, id);
        if (affectedRows == 0) {
            throw new DataAccessException("删除文章失败，没有行被影响");
        }
    }

    @Override
    public void incrementViewCount(Long id) {
        String sql = "UPDATE article SET view_count = view_count + 1 WHERE id = ?";
        executeUpdate(sql, id);
    }

    @Override
    public void incrementLikeCount(Long id) {
        String sql = "UPDATE article SET like_count = like_count + 1 WHERE id = ?";
        executeUpdate(sql, id);
    }

    @Override
    public void decrementLikeCount(Long id) {
        String sql = "UPDATE article SET like_count = GREATEST(0, like_count - 1) WHERE id = ?";
        executeUpdate(sql, id);
    }

    @Override
    public void incrementCommentCount(Long id) {
        String sql = "UPDATE article SET comment_count = comment_count + 1 WHERE id = ?";
        executeUpdate(sql, id);
    }

//    @Override
//    public List<Article> searchByKeyword(String keyword, int limit, int offset) {
//        String sql = "SELECT * FROM article WHERE MATCH (title, content) AGAINST (? IN NATURAL LANGUAGE MODE) LIMIT ? OFFSET ?";
//        return executeQuery(sql, keyword, limit, offset);
//    }
}