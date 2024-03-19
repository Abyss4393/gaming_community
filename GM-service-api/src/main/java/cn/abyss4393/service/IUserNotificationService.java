package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className IUserNotificationService
 * @description TODO
 * @date 2024/3/8
 * @completion false
 */
public interface IUserNotificationService {

    ResultFul<?> getNotifications(Integer id, boolean unread, String type);

    ResultFul<?> read(Integer id);

    ResultFul<?> delete(Integer id);
}
