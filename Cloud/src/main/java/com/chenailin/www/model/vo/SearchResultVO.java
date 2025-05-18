package com.chenailin.www.model.vo;

/**
 * @author evi
 */
public class SearchResultVO {
    private int total;
    private ArticleVO articleVO;
    private KnowledgeBaseVO knowledgeBaseVO;
    private UserVO userVO;

    public SearchResultVO(ArticleVO articleVO, KnowledgeBaseVO knowledgeBaseVO, int total, UserVO userVO) {
        this.articleVO = articleVO;
        this.knowledgeBaseVO = knowledgeBaseVO;
        this.total = total;
        this.userVO = userVO;
    }

    public SearchResultVO() {
    }

    public ArticleVO getArticleVO() {
        return articleVO;
    }

    public void setArticleVO(ArticleVO articleVO) {
        this.articleVO = articleVO;
    }

    public KnowledgeBaseVO getKnowledgeBaseVO() {
        return knowledgeBaseVO;
    }

    public void setKnowledgeBaseVO(KnowledgeBaseVO knowledgeBaseVO) {
        this.knowledgeBaseVO = knowledgeBaseVO;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public UserVO getUserVO() {
        return userVO;
    }

    public void setUserVO(UserVO userVO) {
        this.userVO = userVO;
    }
}