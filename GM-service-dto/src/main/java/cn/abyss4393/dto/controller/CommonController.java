package cn.abyss4393.dto.controller;

import cn.abyss4393.annotation.AuthAccess;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.SimpleCollectionRecord;
import cn.abyss4393.service.impl.ArticleServiceImpl;
import cn.abyss4393.service.impl.CommentServiceImpl;
import cn.abyss4393.service.impl.HistoryServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className CommonController
 * @description TODO
 * @date 21/9/2023
 */
@RestController
@RequestMapping("/api/private/v1/community/common")
public class CommonController {

    @Resource
    private ArticleServiceImpl articleService;

    @Resource
    private HistoryServiceImpl historyService;

    @Resource
    private CommentServiceImpl commentService;

    @AuthAccess(desc = "获取article列表")
    @GetMapping("/article/list")
    public ResultFul<?> getArticleList() throws Exception {
        return articleService.getArticleList();
    }

    @AuthAccess(desc = "通过id获取article的details")
    @GetMapping("/article/{aid}")
    public ResultFul<?> getArticleById(@PathVariable Serializable aid) throws Exception {
        return articleService.getArticleById(aid);
    }


    @AuthAccess(desc = "点赞")
    @GetMapping("/article/upvote/{uid}/{aid}")
    public ResultFul<?> like(@PathVariable Integer uid, @PathVariable Integer aid) {
        return articleService.like(uid, aid);
    }

    @AuthAccess(desc = "取消点赞")
    @GetMapping("/article/upvote/cancel/{uid}/{aid}")
    public ResultFul<?> cancelLike(@PathVariable Integer uid, @PathVariable Integer aid) {
        return articleService.cancelLike(uid, aid);
    }

    @AuthAccess(desc = "拉踩")
    @GetMapping("/article/dislike/{uid}/{aid}")
    public ResultFul<?> dislike(@PathVariable Integer uid, @PathVariable Integer aid) {
        return articleService.dislike(uid, aid);
    }

    @AuthAccess(desc = "取消拉踩")
    @GetMapping("/article/dislike/cancel/{uid}/{aid}")
    public ResultFul<?> addPassiveCount(@PathVariable Integer uid, @PathVariable Integer aid) {
        return articleService.cancelDislike(uid, aid);
    }

    @AuthAccess(desc = "收藏")
    @PostMapping("/article/collect")
    public ResultFul<?> addCollection(@RequestBody SimpleCollectionRecord record) {
        return articleService.addCollect(record.aid(), record.uid());
    }

    @AuthAccess(desc = "取消收藏")
    @PostMapping("/article/collect/cancel")
    public ResultFul<?> cancelCollect(@RequestBody SimpleCollectionRecord record) {
        return articleService.cancelCollect(record.aid(), record.uid());
    }

    @AuthAccess(desc = "历史记录")
    @GetMapping("/history")
    public ResultFul<?> loadHistory() {
        return historyService.loadHistory();
    }

    @AuthAccess(desc = "评论排序")
    @GetMapping("/comment/sort/{aid}/{sort}")
    public ResultFul<?> sortBy(@PathVariable Integer aid, @PathVariable Integer sort) {
        return commentService.sortBy(aid, sort);
    }


    @AuthAccess(desc = "点赞评论")
    @GetMapping("/comment/like")
    public ResultFul<?> commentLike(@RequestParam("id") Integer id, @RequestParam("from") Integer from, @RequestParam("to") Integer to) {
        return commentService.like(id, from, to);
    }

    @AuthAccess(desc = "拉踩评论")
    @GetMapping("/comment/dislike")
    public ResultFul<?> commentDislike(@RequestParam("id") Integer id) {
        return commentService.dislike(id);
    }


    @AuthAccess(desc = "点赞回复")
    @GetMapping("/reply/like")
    public ResultFul<?> replyLike(@RequestParam("id") Integer id, @RequestParam("from") Integer from, @RequestParam("to") Integer to) {
        return commentService.like(id, from, to);
    }

    @AuthAccess(desc = "拉踩回复")
    @GetMapping("/reply/dislike")
    public ResultFul<?> replyDislike(@RequestParam("id") Integer id) {
        return commentService.dislike(id);
    }
}
