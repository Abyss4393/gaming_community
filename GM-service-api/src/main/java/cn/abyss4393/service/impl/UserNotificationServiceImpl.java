package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.UserNotificationMapper;
import cn.abyss4393.po.UserNotification;
import cn.abyss4393.service.IUserNotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className UserNotificationServiceImpl
 * @description TODO
 * @date 2024/3/8
 * @completion false
 */

@Service
public class UserNotificationServiceImpl implements IUserNotificationService {

    @Resource
    private UserNotificationMapper userNotificationMapper;


    @Override
    public ResultFul<?> getNotifications(Integer id, boolean unread, String type) {
        if (StringUtils.checkValNull(id)) return ResultFul.fail(BaseCode.ERROR);
        // 查询通知
        LambdaQueryWrapper<UserNotification> eq = Wrappers.<UserNotification>lambdaQuery().
                eq(UserNotification::getUserId, id);
        if (unread) {
            eq.eq(UserNotification::getIsRead, 0);
        }
        if ("SYSTEM".equals(type)) {
            eq.eq(UserNotification::getType, "ARTICLE").or().
                    eq(UserNotification::getType, "SYSTEM");
        }
        List<UserNotification> userNotifications = userNotificationMapper.selectList(eq);
        // 返回结果
        return ResultFul.success(BaseCode.SUCCESS, userNotifications);
    }

    @Override
    public ResultFul<?> read(Integer id) {
        if (StringUtils.checkValNull(id)) return ResultFul.fail(BaseCode.ERROR);
        LambdaQueryWrapper<UserNotification> userNotificationLambdaQueryWrapper = Wrappers.lambdaQuery();
        userNotificationLambdaQueryWrapper.eq(UserNotification::getId, id);
        UserNotification userNotification = new UserNotification();
        userNotification.setUserId(id);
        userNotification.setIsRead(1);
        userNotificationMapper.update(userNotification, userNotificationLambdaQueryWrapper);
        return ResultFul.success(BaseCode.SUCCESS);
    }

    @Override
    public ResultFul<?> delete(Integer id) {
        int del = userNotificationMapper.deleteById(id);
        userNotificationMapper.sort();
        return 0 != del ? ResultFul.success(BaseCode.DELETE) : ResultFul.fail(BaseCode.DELETE_ERROR);
    }
}
