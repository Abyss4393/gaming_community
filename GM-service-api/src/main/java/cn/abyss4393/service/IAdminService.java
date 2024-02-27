package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.User;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className AdminImpl
 * @description TODO
 * @date 2024/2/3
 * @completion false
 */
public interface IAdminService {

    ResultFul<?> selectBatchUsers(Integer currentPage, Integer pageSize);

    ResultFul<?> selectBatchArticles(Integer currentPage, Integer pageSize);
}
