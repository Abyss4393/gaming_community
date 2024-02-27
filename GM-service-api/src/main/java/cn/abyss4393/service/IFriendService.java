package cn.abyss4393.service;

import cn.abyss4393.entity.ResultFul;


public interface IFriendService {
    ResultFul<?> getFriendListById(Integer uid);

    ResultFul<?> isFriend(Integer uid, Integer friendId);

    ResultFul<?> addFriend(Integer uid, Integer friendId);

}
