package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.Article;
import cn.abyss4393.po.Comment;
import cn.abyss4393.po.Reply;
import cn.abyss4393.vo.ArticleVo;
import cn.abyss4393.vo.CommentVo;
import cn.abyss4393.vo.ReplyVo;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className AdminImpl
 * @description TODO
 * @date 2024/2/3
 * @completion false
 */
public interface IAdminService {

    ResultFul<?> selectBatchUsers(Integer currentPage, Integer pageSize);

    ResultFul<?> selectBatchArticles(Integer currentPage, Integer pageSize);

    ResultFul<?> confirmAuditArticle(Article article);

    ResultFul<?> rejectAuditArticle(ArticleVo articleVo);

    ResultFul<?> confirmAuditComment(Comment comment);

    ResultFul<?> rejectAuditComment(CommentVo commentVo);

    ResultFul<?> confirmAuditReply(Reply reply);

    ResultFul<?> rejectAuditReply(ReplyVo replyVo);

    ResultFul<?> getUpRateArticles();
}
