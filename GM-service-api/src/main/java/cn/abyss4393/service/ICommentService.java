package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.Comment;

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
}
