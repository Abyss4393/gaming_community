package cn.abyss4393.dto.controller;

import cn.abyss4393.annotation.AuthAccess;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.EntryRecommend;
import cn.abyss4393.service.impl.RecommendServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className RecommendController
 * @description TODO
 * @date 2024/3/12
 * @completion false
 */
@RestController
@RequestMapping("/api/private/v1/community/recommend")
public class RecommendController {

    @Resource
    private RecommendServiceImpl recommendService;

    @AuthAccess(desc = "推荐帖子给用户")
    @GetMapping("/for/user")
    public ResultFul<?> recommendForUser(@RequestParam("id") Integer id) {
        return recommendService.forUser(id);
    }

    @AuthAccess(desc = "获取推荐帖子")
    @GetMapping("/list")
    public ResultFul<?> getRecommends() {
        return recommendService.getRecommends();
    }


    @AuthAccess(desc = "设置推荐帖子")
    @PostMapping("/set")
    public ResultFul<?> setRecommend(@RequestBody EntryRecommend entryRecommend) {
        return recommendService.setRecommend(entryRecommend);
    }

    @AuthAccess(desc = "删除推荐帖子")
    @DeleteMapping("/cancel/{aid}")
    public ResultFul<?> deleteById(@PathVariable Integer aid) {
        return recommendService.cancelRecommend(aid);
    }
}
