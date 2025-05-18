package com.chenailin.www.serviceimpl;

import com.chenailin.www.dao.ArticleDao;
import com.chenailin.www.dao.BrowseHistoryDao;
import com.chenailin.www.daoimpl.ArticleDaoImpl;
import com.chenailin.www.daoimpl.BrowseHistoryDaoImpl;
import com.chenailin.www.exception.BusinessException;
import com.chenailin.www.model.pojo.Article;
import com.chenailin.www.model.pojo.BrowseHistory;
import com.chenailin.www.model.vo.BrowseHistoryVO;
import com.chenailin.www.service.BrowseHistoryService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author evi
 */
public class BrowseHistoryServiceImpl implements BrowseHistoryService {
    private final BrowseHistoryDao browseHistoryDao = new BrowseHistoryDaoImpl();
    private final ArticleDao articleDao = new ArticleDaoImpl();

    @Override
    public void recordBrowseHistory(Long userId, Long articleId) {
        // 验证文章存在
        Article article = articleDao.findById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        // 记录浏览历史
        BrowseHistory history = new BrowseHistory();
        history.setUserId(userId);
        history.setArticleId(articleId);
        history.setBrowseTime(new Date());

        browseHistoryDao.save(history);
    }

    @Override
    public List<BrowseHistoryVO> getUserBrowseHistory(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        List<BrowseHistory> historyList = browseHistoryDao.findByUserId(userId, size, offset);

        // 转换为VO对象
        return historyList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBrowseHistory(Long id, Long userId) {
        // TODO: 验证是否是用户自己的浏览记录
        browseHistoryDao.delete(id);
    }

    @Override
    public void clearBrowseHistory(Long userId) {
        browseHistoryDao.deleteByUserId(userId);
    }

    // BrowseHistoryService methods implementation

    /**
     * Clear all browse history for a user
     * @param userId User ID
     */
    @Override
    public void clearUserBrowseHistory(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }

        // 删除用户的所有浏览记录
        int deletedCount = browseHistoryDao.deleteByUserId(userId);
    }

    private BrowseHistoryVO convertToVO(BrowseHistory history) {
        BrowseHistoryVO vo = new BrowseHistoryVO();
        vo.setId(history.getId());
        vo.setUserId(history.getUserId());
        vo.setArticleId(history.getArticleId());
        vo.setBrowseTime(history.getBrowseTime());

        // 获取文章信息
        Article article = articleDao.findById(history.getArticleId());
        if (article != null) {
            vo.setArticleTitle(article.getTitle());
            vo.setAuthorId(article.getAuthorId());
            // TODO: 设置作者信息（需要UserDao支持）
        }

        return vo;
    }

}