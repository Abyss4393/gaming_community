package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.MessageMapper;
import cn.abyss4393.mapper.UserMapper;
import cn.abyss4393.po.Message;
import cn.abyss4393.service.IMessageService;
import cn.abyss4393.utils.redis.RedisUtils;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className MessageServiceImpl
 * @description TODO
 * @date 2023/11/14
 * @completion false
 */
@Service
public class MessageServiceImpl implements IMessageService {
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private UserMapper userMapper;
    @Resource
    private MessageMapper messageMapper;

    @Override
    public ResultFul<?> getChatRecord(Integer senderId, Integer receiverId) {
        Set<Object> history = new HashSet<>();
        String fromKey = "senderId:" + senderId + "receiverId:" + receiverId;
        String toKey = "senderId:" + senderId + "receiverId:" + receiverId;
        Optional<List<Object>> fromRedisData = Optional.ofNullable(redisUtils.lGet(fromKey, 0, -1));
        Optional<List<Object>> toRedisData = Optional.ofNullable(redisUtils.lGet(fromKey, 0, -1));
        fromRedisData.ifPresent(history::addAll);
        toRedisData.ifPresent(history::addAll);
        LambdaQueryWrapper<Message> messageLambdaQueryWrapper = Wrappers.lambdaQuery();
        messageLambdaQueryWrapper.eq(Message::getSenderId, senderId).eq(Message::getReceiverId, receiverId);
        JSONArray fromArray = JSONUtil.parseArray(messageMapper.selectOne(messageLambdaQueryWrapper));
        history.addAll(fromArray);
        messageLambdaQueryWrapper.clear();
        messageLambdaQueryWrapper.eq(Message::getSenderId, receiverId).eq(Message::getReceiverId, senderId);
        JSONArray toArray = JSONUtil.parseArray(messageMapper.selectOne(messageLambdaQueryWrapper));
        history.addAll(toArray);
        return ResultFul.success(BaseCode.SUCCESS, history);
    }
}
