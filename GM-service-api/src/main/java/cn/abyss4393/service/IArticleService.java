package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.Article;

public interface IArticleService {

    ResultFul<?> postArticle(Article article) throws Exception;

    ResultFul<?> addPositivenessCount(Integer aid) throws Exception;

    ResultFul<?> addPassivenessCount(Integer aid) throws Exception;

    ResultFul<?> addCollect(Integer id, Integer uid) throws Exception;
}
