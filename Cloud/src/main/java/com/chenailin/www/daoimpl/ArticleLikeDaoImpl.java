package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.ArticleLikeDao;
import com.chenailin.www.model.pojo.ArticleLike;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 文章点赞数据访问实现
 * @author evi
 */
public class ArticleLikeDaoImpl extends BaseDao<ArticleLike> implements ArticleLikeDao {

    @Override
    protected ArticleLike mapResultSet(ResultSet rs) throws SQLException {
        ArticleLike like = new ArticleLike();
        like.setId(rs.getLong("id"));
        like.setArticleId(rs.getLong("article_id"));
        like.setUserId(rs.getLong("user_id"));
        like.setCreateTime(rs.getTimestamp("create_time"));
        return like;
    }

    @Override
    public Long save(ArticleLike articleLike) {
        String sql = "INSERT INTO article_likes (article_id, user_id, create_time) VALUES (?, ?, ?)";
        return executeInsertWithGeneratedKey(sql, articleLike.getArticleId(), articleLike.getUserId(), new java.sql.Timestamp(articleLike.getCreateTime().getTime()));
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM article_likes WHERE id = ?";
        executeUpdate(sql, id);
    }

    @Override
    public ArticleLike findByArticleAndUser(Long articleId, Long userId) {
        String sql = "SELECT * FROM article_likes WHERE article_id = ? AND user_id = ?";
        return executeQuerySingle(sql, articleId, userId);
    }

    @Override
    public List<ArticleLike> findByArticleId(Long articleId) {
        String sql = "SELECT * FROM article_likes WHERE article_id = ?";
        return executeQuery(sql, articleId);
    }

    @Override
    public List<ArticleLike> findByUserId(Long userId) {
        String sql = "SELECT * FROM article_likes WHERE user_id = ?";
        return executeQuery(sql, userId);
    }

    @Override
    public int deleteByArticleId(Long articleId) {
        String sql = "DELETE FROM article_likes WHERE article_id = ?";
        return executeUpdate(sql, articleId);
    }

    @Override
    public int deleteByUserId(Long userId) {
        String sql = "DELETE FROM article_likes WHERE user_id = ?";
        return executeUpdate(sql, userId);
    }

    @Override
    public int countByArticleId(Long articleId) {
        String sql = "SELECT COUNT(*) FROM article_likes WHERE article_id = ?";
        return executeCountQuery(sql, articleId);
    }
}