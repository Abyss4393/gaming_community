package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.Reply;

import java.io.Serializable;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className IReplyService
 * @description TODO
 * @date 26/11/2023
 * @completion false
 */
public interface IReplyService {
    ResultFul<?> postReply(Reply reply);

    ResultFul<?> getRepliesByUId(Integer uid);

    ResultFul<?> getRepliesTreeByUID(Integer aid, Integer commentId, Integer replyId);

    ResultFul<?> getBatchReplies(Integer currentPage, Integer pageSize);


    ResultFul<?> searchReply(String keyword, Integer currentPage, Integer pageSize);

    ResultFul<?> deleteReply(Serializable id);

    ResultFul<?> like(Integer id, Integer from, Integer to);

    ResultFul<?> dislike(Integer id);
}
