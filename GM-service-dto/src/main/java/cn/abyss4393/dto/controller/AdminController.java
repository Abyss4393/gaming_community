package cn.abyss4393.dto.controller;

import cn.abyss4393.annotation.AuthAccess;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.*;
import cn.abyss4393.service.*;
import cn.abyss4393.vo.ArticleVo;
import cn.abyss4393.vo.CommentVo;
import cn.abyss4393.vo.ReplyVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className AdminController
 * @description TODO
 * @date 2024/2/3
 * @completion false
 */

@RestController
@RequestMapping("/api/private/v1/community/admin")
public class AdminController {
    @Resource
    private IAdminService adminService;

    @Resource
    private IUserService userService;

    @Resource
    private IArticleService articleService;

    @Resource
    private ICommentService commentService;

    @Resource
    private IReplyService replyService;

    @AuthAccess(desc = "获取用户列表")
    @GetMapping("/user/page")
    public ResultFul<?> selectBatchUser(@RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                        @RequestParam(value = "pageSize", required = false, defaultValue = "9") Integer pageSize) {
        return adminService.selectBatchUsers(currentPage, pageSize);
    }

    @AuthAccess(desc = "搜索用户")
    @PostMapping("/user/search")
    public ResultFul<?> searchUser(@RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "9") Integer pageSize, @RequestBody Keyword keyword) {

        return userService.searchUser(keyword.keyword(), currentPage, pageSize);

    }

    @AuthAccess(desc = "修改用户")
    @PostMapping("/user/modify")
    public ResultFul<?> modifyUser(@RequestBody User user) {
        return userService.modifyUser(user);
    }

    @AuthAccess(desc = "删除用户")
    @DeleteMapping("/user/delete")
    public ResultFul<?> deleteUser(@RequestParam("id") Integer id) {
        return userService.deleteUser(id.longValue());
    }

    @AuthAccess(desc = "批量获取系统帖子")
    @GetMapping("/article/page")
    public ResultFul<?> selectBatchArticle(@RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                           @RequestParam(value = "pageSize", required = false, defaultValue = "9") Integer pageSize) {
        return adminService.selectBatchArticles(currentPage, pageSize);
    }

    @AuthAccess(desc = "搜索帖子")
    @PostMapping("/article/search")
    public ResultFul<?> searchArticle(@RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "9") Integer pageSize,
                                      @RequestBody Keyword keyword) {
        return articleService.searchArticles(keyword.keyword(), currentPage, pageSize);
    }



    @AuthAccess(desc = "获取未审核帖子")
    @GetMapping("/audit/article/unchecked")
    public ResultFul<?> getUncheckedArticles(@RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return articleService.findUnapprovedArticles(currentPage, pageSize);
    }

    @AuthAccess(desc = "通过审核帖子")
    @PostMapping("/audit/article/confirm")
    public ResultFul<?> approved(@RequestBody Article article) {

        return adminService.confirmAuditArticle(article);
    }

    @AuthAccess(desc = "驳回帖子")
    @PostMapping("/audit/article/reject")
    public ResultFul<?> reject(@RequestBody ArticleVo articleVo) {
        return adminService.rejectAuditArticle(articleVo);
    }

    @AuthAccess(desc = "删除帖子")
    @DeleteMapping("/article/delete")
    public ResultFul<?> deleteArticle(@RequestParam("id") Integer id) throws Exception {
        return articleService.deleteArticleById(id);
    }


    @AuthAccess(desc = "批量获取用户评论")
    @GetMapping("/comment/page")
    public ResultFul<?> getBatchComments(@RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                         @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return commentService.getBatchComments(currentPage, pageSize);
    }

    @AuthAccess(desc = "搜索评论")
    @PostMapping("/comment/search")
    public ResultFul<?> searchComment(@RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "9") Integer pageSize,
                                      @RequestBody Keyword keyword) {
        return commentService.searchComment(keyword.keyword(), currentPage, pageSize);
    }


    @AuthAccess(desc = "通过审核评论")
    @PostMapping("/audit/comment/confirm")
    public ResultFul<?> approvedComment(@RequestBody Comment comment) {
        return adminService.confirmAuditComment(comment);
    }

    @AuthAccess(desc = "驳回评论")
    @PostMapping("/audit/comment/reject")
    public ResultFul<?> reject(@RequestBody CommentVo commentVo) {
        return adminService.rejectAuditComment(commentVo);
    }

    @AuthAccess(desc = "删除评论")
    @DeleteMapping("/comment/delete/{aid}/{uid}")
    public ResultFul<?> deleteComment(@PathVariable String aid, @PathVariable String uid) throws Exception {
        return commentService.delCommentByIds(aid, uid);
    }


    @AuthAccess(desc = "批量获取用户回复")
    @GetMapping("/reply/page")
    public ResultFul<?> getBatchReplies(@RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return replyService.getBatchReplies(currentPage, pageSize);
    }

    @AuthAccess(desc = "搜索回复")
    @PostMapping("/reply/search")
    public ResultFul<?> searchReply(@RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                    @RequestParam(value = "pageSize", required = false, defaultValue = "9") Integer pageSize,
                                    @RequestBody Keyword keyword) {
        return replyService.searchReply(keyword.keyword(), currentPage, pageSize);
    }


    @AuthAccess(desc = "通过审核回复")
    @PostMapping("/audit/reply/confirm")
    public ResultFul<?> approvedReply(@RequestBody Reply reply) {
        return adminService.confirmAuditReply(reply);
    }

    @AuthAccess(desc = "驳回回复")
    @PostMapping("/audit/reply/reject")
    public ResultFul<?> rejectReply(@RequestBody ReplyVo replyVo) {
        return adminService.rejectAuditReply(replyVo);
    }

    @AuthAccess(desc = "删除回复")
    @DeleteMapping("/reply/delete/{id}")
    public ResultFul<?> deleteReply(@PathVariable Integer id) {
        return replyService.deleteReply(id);
    }

    @AuthAccess(desc = "获取综合评分较高的帖子")
    @GetMapping("/recommend/uprate")
    public ResultFul<?> getUpRateArticle() {
        return adminService.getUpRateArticles();
    }
}
