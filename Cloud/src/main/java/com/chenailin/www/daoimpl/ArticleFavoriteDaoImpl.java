package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.ArticleFavoriteDao;
import com.chenailin.www.model.pojo.ArticleFavorite;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 文章点赞数据访问实现
 * @author evi
 */
public class ArticleFavoriteDaoImpl extends BaseDao<ArticleFavorite> implements ArticleFavoriteDao {

    @Override
    protected ArticleFavorite mapResultSet(ResultSet rs) throws SQLException {
        ArticleFavorite favorite = new ArticleFavorite();
        favorite.setId(rs.getLong("id"));
        favorite.setArticleId(rs.getLong("article_id"));
        favorite.setUserId(rs.getLong("user_id"));
        favorite.setCreateTime(rs.getTimestamp("create_time"));
        return favorite;
    }

    @Override
    public Long save(ArticleFavorite articleFavorite) {
        String sql = "INSERT INTO article_favorite (article_id, user_id, create_time) VALUES (?, ?, ?)";
        return executeInsertWithGeneratedKey(sql, articleFavorite.getArticleId(), articleFavorite.getUserId(),
                new java.sql.Timestamp(articleFavorite.getCreateTime().getTime()));
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM article_favorite WHERE id = ?";
        executeUpdate(sql, id);
    }

    @Override
    public ArticleFavorite findByArticleAndUser(Long articleId, Long userId) {
        String sql = "SELECT * FROM article_favorite WHERE article_id = ? AND user_id = ?";
        return executeQuerySingle(sql, articleId, userId);
    }

    @Override
    public List<ArticleFavorite> findByArticleId(Long articleId) {
        String sql = "SELECT * FROM article_favorite WHERE article_id = ?";
        return executeQuery(sql, articleId);
    }

    @Override
    public List<ArticleFavorite> findByUserId(Long userId) {
        String sql = "SELECT * FROM article_favorite WHERE user_id = ?";
        return executeQuery(sql, userId);
    }

    @Override
    public int deleteByArticleId(Long articleId) {
        String sql = "DELETE FROM article_favorite WHERE article_id = ?";
        return executeUpdate(sql, articleId);
    }

    @Override
    public int deleteByUserId(Long userId) {
        String sql = "DELETE FROM article_favorite WHERE user_id = ?";
        return executeUpdate(sql, userId);
    }

    @Override
    public int countByArticleId(Long articleId) {
        String sql = "SELECT COUNT(*) FROM article_favorite WHERE article_id = ?";
        return executeCountQuery(sql, articleId);
    }
}