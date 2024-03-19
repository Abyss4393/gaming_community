package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.Article;

import java.io.Serializable;

public interface IArticleService {

    ResultFul<?> getArticleById(Serializable aid) throws Exception;

    ResultFul<?> getArticleListByPid(Serializable pid) throws Exception;

    ResultFul<?> getArticleList() throws Exception;

    ResultFul<?> postArticle(Article article) throws Exception;

    ResultFul<?> like(Integer uid, Integer aid) throws Exception;

    ResultFul<?> dislike(Integer uid, Integer aid) throws Exception;

    ResultFul<?> addCollect(Integer aid, Integer uid) throws Exception;

    ResultFul<?> cancelLike(Integer uid, Integer aid);

    ResultFul<?> cancelDislike(Integer uid, Integer aid);

    ResultFul<?> cancelCollect(Integer aid, Integer uid);

    ResultFul<?> searchArticles(String keyword, Integer currentPage, Integer pageSize);

    ResultFul<?> findUnapprovedArticles(Integer currentPage, Integer pageSize);

    ResultFul<?> deleteArticleById(Serializable id) throws Exception;
}
