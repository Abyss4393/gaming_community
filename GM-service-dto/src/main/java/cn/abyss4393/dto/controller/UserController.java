package cn.abyss4393.dto.controller;

import cn.abyss4393.annotation.AuthAccess;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.Article;
import cn.abyss4393.po.Comment;
import cn.abyss4393.po.User;
import cn.abyss4393.service.impl.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

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

    @Resource
    private CollectionServiceImpl collectionService;

    @Resource
    private FileServiceImpl fileService;

    @Resource
    private  CommentServiceImpl commentService;

    @Resource
    private UpvoteServiceImpl upvoteService;

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

    @AuthAccess(desc = "获取用户帖子列表")
    @GetMapping("/article/list/{uid}")
    @ResponseBody
    public ResultFul<?> getArticleListById(@PathVariable Serializable uid) throws Exception {
        return articleService.getArticleListByPid(uid);
    }

    @AuthAccess(desc = "用户点赞帖子状态")
    @GetMapping("/article/upvote/{uid}/{aid}")
    @ResponseBody
    public ResultFul<?> upvoteStatus(@PathVariable Serializable uid, @PathVariable Serializable aid) {
        return upvoteService.isUpvote(uid,aid);
    }

    @AuthAccess(desc = "用户收藏帖子状态")
    @GetMapping("/article/collect/{uid}/{aid}")
    @ResponseBody
    public ResultFul<?> collectStatus(@PathVariable Serializable uid, @PathVariable Serializable aid) {
        return collectionService.isCollected(uid,aid);
    }


    @AuthAccess(desc = "上传头像")
    @PostMapping("/upload/avatar")
    @ResponseBody
    public ResultFul<?> uploadAvatar(MultipartFile file) throws IOException {
        return fileService.uploadAvatar(file);
    }

    @AuthAccess(desc = "更新用户信息")
    @PostMapping("/update")
    @ResponseBody
    public ResultFul<?> update(@RequestBody User user) {
        return userServiceImpl.update(Objects.requireNonNull(user));
    }


    @AuthAccess(desc = "获取用户收藏的帖子")
    @GetMapping("/collect/{uid}")
    @ResponseBody
    public ResultFul<?> getCollectionsByUid(@PathVariable Serializable uid) {
        return collectionService.getArticleByCollection(uid);
    }




    @AuthAccess(desc = "用户评论")
    @PostMapping("/comment")
    @ResponseBody
    public ResultFul<?> comment(@RequestBody Comment comment) {
        return commentService.postComment(comment);
    }
}
