package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.BaseObj;
import cn.abyss4393.po.Manager;
import cn.abyss4393.po.User;

import java.util.List;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className AbstractService
 * @description TODO
 * @date 3/9/2023
 */
public interface AbstractService {
    <T extends BaseObj> ResultFul<?> login(final T t);

    ResultFul<?> login(User user);

    ResultFul<?> login(Manager manager);

    ResultFul<?> exit(Integer uid);

    <T extends BaseObj> ResultFul<?> register(T t);


    ResultFul<?> register(Manager manager);

    ResultFul<?> register(User user);

    boolean modify();

    List<User> selectAll();

    boolean logout();
}
