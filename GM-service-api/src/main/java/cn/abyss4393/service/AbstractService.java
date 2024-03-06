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
public interface AbstractService<E>  {

    ResultFul<?> login(E entity);

    ResultFul<?> register(E entity);
    ResultFul<?> exit(Integer uid);

}
