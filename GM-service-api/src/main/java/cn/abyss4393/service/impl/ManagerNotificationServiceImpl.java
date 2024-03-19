package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.ManagerNotificationMapper;
import cn.abyss4393.po.ManagerNotification;
import cn.abyss4393.service.IManagerNotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ManagerNotificationServiceImpl
 * @description TODO
 * @date 2024/3/19
 * @completion false
 */
@Service

public class ManagerNotificationServiceImpl implements IManagerNotificationService {

    @Resource
    private ManagerNotificationMapper managerNotificationMapper;

    @Override
    public ResultFul<?> getNotifications() {
        List<ManagerNotification> managerNotifications = managerNotificationMapper.selectList(null);
        return ResultFul.success(BaseCode.SUCCESS, managerNotifications);
    }

    @Override
    public ResultFul<?> cope(Integer id) {
        LambdaQueryWrapper<ManagerNotification> managerNotificationLambdaQueryWrapper = Wrappers.lambdaQuery();
        managerNotificationLambdaQueryWrapper.eq(ManagerNotification::getId, id);
        ManagerNotification managerNotification = new ManagerNotification();
        managerNotification.setId(id);
        managerNotification.setIsRead(1);
        managerNotificationMapper.update(managerNotification, managerNotificationLambdaQueryWrapper);
        return ResultFul.success(BaseCode.SUCCESS);
    }

    @Override
    public ResultFul<?> delete(Integer id) {
        int del = managerNotificationMapper.deleteById(id);
        managerNotificationMapper.sort();
        return 0!= del ? ResultFul.success(BaseCode.DELETE) : ResultFul.fail(BaseCode.DELETE_ERROR);
    }
}
