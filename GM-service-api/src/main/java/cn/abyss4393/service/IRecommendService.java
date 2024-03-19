package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className IRecommendService
 * @description TODO
 * @date 2024/3/12
 * @completion false
 */
public interface IRecommendService {

    ResultFul<?> forUser(Integer id);
}
