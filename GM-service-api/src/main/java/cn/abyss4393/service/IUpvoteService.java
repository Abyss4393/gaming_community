package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;

import java.io.Serializable;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className IUpvoteService
 * @description TODO
 * @date 20/11/2023
 * @completion false
 */
public interface IUpvoteService {
    ResultFul<?> isUpvote(Serializable uid,Serializable aid);
}
