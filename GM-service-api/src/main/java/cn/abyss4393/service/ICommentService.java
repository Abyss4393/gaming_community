package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.Comment;

import java.io.Serializable;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ICommentService
 * @description TODO
 * @date 19/11/2023
 * @completion false
 */
public interface ICommentService {
    ResultFul<?> postComment(Comment comment);

    ResultFul<?> getCommentByUid(Serializable uid);

    ResultFul<?> getBatchComments(Integer currentPage, Integer pageSize);

    ResultFul<?> delCommentByIds(Serializable aid, Serializable uid);

    ResultFul<?> searchComment(String keyword, Integer currentPage, Integer pageSize);

    ResultFul<?> sortBy(Integer id, Integer sortTypeDesc);

    ResultFul<?> like(Integer id, Integer from, Integer to);

    ResultFul<?> dislike(Integer id);
}
