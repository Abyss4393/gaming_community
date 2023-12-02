package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.FriendListMapper;
import cn.abyss4393.po.FriendList;
import cn.abyss4393.service.IFriendService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className FriendServiceImpl
 * @description TODO
 * @date 5/9/2023
 */

@Service
public class FriendServiceImpl implements IFriendService {

    @SuppressWarnings("all")
    @Autowired
    private FriendListMapper friendListMapper;

    @Override
    public ResultFul<?> getFriendListById(Integer uid) {
        if (StringUtils.checkValNull(uid)) return ResultFul.fail(BaseCode.VALUE_NULL);
        List<Integer> friendsId = friendListMapper.getFriendsIdByRootId(uid);
        return ResultFul.success(BaseCode.SEARCH, new HashMap<>() {{
            this.put("uid", uid);
            this.put("friends_id", friendsId);
        }});
    }

    @Override
    public ResultFul<?> addFriend(Integer uid, Integer friendId) {
        if (StringUtils.checkValNull(uid) || StringUtils.checkValNull(friendId))
            return ResultFul.fail(BaseCode.MISS_PARAMS);
        LambdaQueryWrapper<FriendList> existLambda = new LambdaQueryWrapper<>();
        existLambda.eq(FriendList::getRootId, uid);
        existLambda.eq(FriendList::getUserId, friendId);
        final boolean exists = friendListMapper.exists(existLambda);
        if (!exists) {
            FriendList friendList = new FriendList();
            friendList.setId(Math.toIntExact(friendListMapper.selectCount(null) + 1));
            friendList.setRootId(uid);
            friendList.setUserId(friendId);
            int insert = friendListMapper.insert(friendList);
            if (insert != 0)
                ResultFul.success(BaseCode.ATTENTION_SUCCESS);
        }
        return ResultFul.fail(BaseCode.ATTENTION_FAIL);
    }

}
