package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.User;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className IUserService
 * @description TODO
 * @date 2024/2/14
 * @date 2024/2/14
 * @completion false
 */
public interface IUserService {
    ResultFul<?> searchUser(String keyword, Integer pageNum, Integer pageSize);

    ResultFul<?> modifyUser(User user);
    ResultFul<?> deleteUser(Long id);
}
