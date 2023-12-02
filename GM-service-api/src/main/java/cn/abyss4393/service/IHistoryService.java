package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className IHistoryService
 * @description TODO
 * @date 29/11/2023
 * @completion false
 */
public interface IHistoryService {
    ResultFul<?> loadHistory();
    ResultFul<?> saveHistory(String keyword);
}
