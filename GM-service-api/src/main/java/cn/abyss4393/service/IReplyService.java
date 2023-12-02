package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;

import java.io.Serializable;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className IReplyService
 * @description TODO
 * @date 26/11/2023
 * @completion false
 */
public interface IReplyService {
    ResultFul<?> getRepliesByUId(Serializable uid);
}
