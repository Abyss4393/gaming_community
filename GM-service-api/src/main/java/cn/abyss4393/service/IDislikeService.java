package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;

import java.io.Serializable;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className IDislikeService
 * @description TODO
 * @date 2024/3/11
 * @completion false
 */
public interface IDislikeService {


    ResultFul<?> isDislike(Serializable uid, Serializable aid);
}
