package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.po.EntryRecommend;

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

    ResultFul<?> getRecommends();

    ResultFul<?> setRecommend(EntryRecommend recommend);
    ResultFul<?> cancelRecommend(Integer aid);
}
