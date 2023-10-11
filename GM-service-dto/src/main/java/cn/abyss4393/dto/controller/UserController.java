package cn.abyss4393.dto.controller;

import cn.abyss4393.annotation.AuthAccess;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.Article;
import cn.abyss4393.po.User;
import cn.abyss4393.service.impl.ArticleServiceImpl;
import cn.abyss4393.service.impl.FriendServiceImpl;
import cn.abyss4393.service.impl.UserServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className UserController
 * @description TODO
 * @date 3/9/2023
 */
@Controller
@RequestMapping("/api/private/v1/community/user")
public class UserController {

    @Resource
    private UserServiceImpl userServiceImpl;

    @Resource
    private FriendServiceImpl friendServiceImpl;

    @Resource
    private ArticleServiceImpl articleService;

    @AuthAccess(desc = "用户注册")
    @PostMapping("/register")
    @ResponseBody
    public ResultFul<?> register(@RequestBody User user) {
        return userServiceImpl.register(user);
    }

    @AuthAccess(desc = "用户登录")
    @PostMapping("/login")
    @ResponseBody
    public ResultFul<?> login(@RequestBody User user) {
        return userServiceImpl.login(user);
    }

    @AuthAccess(desc = "删除token")
    @GetMapping("/exit/{uid}")
    @ResponseBody
    public ResultFul<?> exit(@PathVariable Integer uid) {
        return userServiceImpl.exit(uid);
    }

    @AuthAccess(desc = "获取用户好友")
    @GetMapping("/friends/{uid}")
    @ResponseBody
    public ResultFul<?> getFriends(@PathVariable Integer uid) {
        return friendServiceImpl.getFriendListById(uid);
    }

    @AuthAccess(desc = "获取用户信息")
    @GetMapping("/info")
    @ResponseBody
    public ResultFul<?> getUserInfo(@RequestParam("uid") Integer uid) {
        return userServiceImpl.getUserInfoByUId(uid);
    }


    @AuthAccess(desc = "发帖")
    @PostMapping("/article/post")
    @ResponseBody
    public ResultFul<?> postArticle(@RequestBody Article article) {
        return articleService.postArticle(article);
    }
}
