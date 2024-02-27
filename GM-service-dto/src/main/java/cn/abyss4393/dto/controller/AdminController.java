package cn.abyss4393.dto.controller;

import cn.abyss4393.annotation.AuthAccess;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.Keyword;
import cn.abyss4393.po.User;
import cn.abyss4393.service.IAdminService;
import cn.abyss4393.service.IArticleService;
import cn.abyss4393.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className AdminController
 * @description TODO
 * @date 2024/2/3
 * @completion false
 */

@Controller
@RequestMapping("/api/private/v1/community/admin")
public class AdminController {
    @Autowired
    private IAdminService adminService;

    @Autowired
    private IUserService userService;

    @Resource
    private IArticleService articleService;

    @AuthAccess(desc = "获取用户列表")
    @GetMapping("/user/page")
    @ResponseBody
    public ResultFul<?> selectBatchUser(@RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                        @RequestParam(value = "pageSize", required = false, defaultValue = "9") Integer pageSize) {
        return adminService.selectBatchUsers(currentPage, pageSize);
    }

    @AuthAccess(desc = "搜索用户")
    @PostMapping("/user/search")
    @ResponseBody
    public ResultFul<?> searchUser(@RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "9") Integer pageSize, @RequestBody Keyword keyword) {

        return userService.searchUser(keyword.keyword(), currentPage, pageSize);

    }

    @AuthAccess(desc = "修改用户")
    @PostMapping("/user/modify")
    @ResponseBody
    public ResultFul<?> modifyUser(@RequestBody User user) {
        return userService.modifyUser(user);
    }

    @AuthAccess(desc = "删除用户")
    @DeleteMapping("/user/delete")
    @ResponseBody
    public ResultFul<?> deleteUser(@RequestParam("id") Integer id) {
        return userService.deleteUser(id.longValue());
    }

    @AuthAccess(desc = "批量获取系统帖子")
    @GetMapping("/article/page")
    @ResponseBody
    public ResultFul<?> selectBatchArticle(@RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                           @RequestParam(value = "pageSize", required = false, defaultValue = "9") Integer pageSize) {
        return adminService.selectBatchArticles(currentPage, pageSize);
    }

    @AuthAccess(desc = "搜索帖子")
    @PostMapping("/article/search")
    @ResponseBody
    public ResultFul<?> searchArticle(@RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "9") Integer pageSize,
                                      @RequestBody Keyword keyword) {
        return articleService.searchArticles(keyword.keyword(), currentPage, pageSize);
    }


    @AuthAccess(desc = "删除帖子")
    @DeleteMapping("/article/delete")
    @ResponseBody
    public ResultFul<?> deleteArticle(@RequestParam("id") Integer id) throws Exception {
        return articleService.deleteArticleById(id);
    }
}
