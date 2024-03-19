package cn.abyss4393.dto.controller;

import cn.abyss4393.annotation.AuthAccess;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.service.impl.RecommendServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
