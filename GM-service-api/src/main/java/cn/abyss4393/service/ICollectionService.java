package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;

import java.io.Serializable;

public interface ICollectionService {
    ResultFul<?> getArticleByCollection(Serializable uid);
}
