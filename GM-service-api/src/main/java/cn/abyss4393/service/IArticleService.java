package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.Article;

import java.io.Serializable;

public interface IArticleService {

    ResultFul<?> getArticleById(Serializable aid) throws Exception;

    ResultFul<?> getArticleListByPid(Serializable pid) throws Exception;
    ResultFul<?> getArticleList() throws Exception;

    ResultFul<?> postArticle(Article article) throws Exception;

    ResultFul<?> addPositivenessCount(Integer uid, Integer aid) throws Exception;

    ResultFul<?> addPassivenessCount(Integer aid) throws Exception;

    ResultFul<?> addCollect(Integer id, Integer uid) throws Exception;


    ResultFul<?> searchArticles(String keyword,Integer currentPage,Integer pageSize);
    ResultFul<?> deleteArticleById(Serializable id) throws Exception;
}
