package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className IMessageService
 * @description TODO
 * @date 2023/11/14
 * @completion false
 */
public interface IMessageService {
    ResultFul<?> getChatRecord(Integer from,Integer to);

}
