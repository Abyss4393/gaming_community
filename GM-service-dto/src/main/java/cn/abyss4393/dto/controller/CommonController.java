package cn.abyss4393.dto.controller;

import cn.abyss4393.annotation.AuthAccess;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.SimpleCollectionRecord;
import cn.abyss4393.service.impl.ArticleServiceImpl;
import cn.abyss4393.service.impl.HistoryServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className CommonController
 * @description TODO
 * @date 21/9/2023
 */
@Controller
@RequestMapping("/api/private/v1/community/common")
public class CommonController {

    @Resource
    private ArticleServiceImpl articleService;

    @Resource
    private HistoryServiceImpl historyService;

    @AuthAccess(desc = "获取article列表")
    @GetMapping("/article/list")
    @ResponseBody
    public ResultFul<?> getArticleList() throws Exception {
        return articleService.getArticleList();
    }

    @AuthAccess(desc = "通过id获取article的details")
    @GetMapping("/article/{aid}")
    @ResponseBody
    public ResultFul<?> getArticleById(@PathVariable Serializable aid) throws Exception {
        return articleService.getArticleById(aid);
    }


    @AuthAccess(desc = "点赞")
    @GetMapping("/article/upvote/{uid}/{aid}")
    @ResponseBody
    public ResultFul<?> addPositivenessCount(@PathVariable Integer uid, @PathVariable Integer aid) {
        return articleService.addPositivenessCount(uid,aid);
    }

    @AuthAccess(desc = "拉踩")
    @GetMapping("/article/trample")
    @ResponseBody
    public ResultFul<?> addPassiveCount(@RequestParam("aid") Integer aid) {
        return articleService.addPassivenessCount(aid);
    }

    @AuthAccess(desc = "收藏")
    @PostMapping("/article/collect")
    @ResponseBody
    public ResultFul<?> addCollection(@RequestBody SimpleCollectionRecord record) {
        return articleService.addCollect(record.aid(), record.uid());
    }

    @AuthAccess(desc = "历史记录")
    @GetMapping("/history")
    @ResponseBody
    public ResultFul<?> loadHistory(){
        return historyService.loadHistory();
    }
}
