package cn.abyss4393.dto.controller;

import cn.abyss4393.annotation.AuthAccess;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.service.IManagerNotificationService;
import cn.abyss4393.service.IUserNotificationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className NotificationController
 * @description TODO
 * @date 2024/3/8
 * @completion false
 */

@RestController
@RequestMapping("/api/private/v1/community/sys")
public class NotificationController {


    @Resource
    private IUserNotificationService userNotificationService;

    @Resource
    private IManagerNotificationService managerNotificationService;


    @AuthAccess(desc = "获取用户通知")
    @GetMapping("/notification/user/{id}")
    public ResultFul<?> getNotificationListByUserId(@PathVariable Integer id, @RequestParam("unread") boolean unread, @RequestParam("type") String type) {
        return userNotificationService.getNotifications(id, unread, type);
    }

    @AuthAccess(desc = "已读")
    @GetMapping("/notification/read/{id}")
    public ResultFul<?> readForUser(@PathVariable Integer id) {
        return userNotificationService.read(id);
    }

    @AuthAccess(desc = "删除用户通知")
    @DeleteMapping("/notification/delete/{id}")
    public ResultFul<?> deleteUserNotification(@PathVariable Integer id) {
        return userNotificationService.delete(id);
    }

    @AuthAccess(desc = "获取管理员通知")
    @GetMapping("/notification/manager")
    public ResultFul<?> getManagerNotifications() {
        return managerNotificationService.getNotifications();
    }

    @AuthAccess(desc = "处理")
    @GetMapping("/notification/cope/{id}")
    public ResultFul<?> cope(@PathVariable Integer id) {
        return managerNotificationService.cope(id);
    }

    @AuthAccess(desc = "删除管理员通知")
    @DeleteMapping("/notification/manager/delete/{id}")
    public ResultFul<?> deleteManagerNotification(@PathVariable Integer id) {
        return managerNotificationService.delete(id);
    }
}
