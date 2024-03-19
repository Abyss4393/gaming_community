package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className IManagerNotificationService
 * @description TODO
 * @date 2024/3/19
 * @completion false
 */
public interface IManagerNotificationService {
    ResultFul<?> getNotifications();

    ResultFul<?> cope(Integer id);

    ResultFul<?> delete(Integer id);
}
