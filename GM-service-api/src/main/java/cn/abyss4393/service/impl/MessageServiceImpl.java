package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.MessageMapper;
import cn.abyss4393.po.Message;
import cn.abyss4393.service.IMessageService;
import cn.abyss4393.utils.redis.RedisUtils;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className MessageServiceImpl
 * @description TODO
 * @date 2023/11/14
 * @completion false
 */
@Service
@Slf4j
public class MessageServiceImpl implements IMessageService {
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private MessageMapper messageMapper;

    @Override
    public ResultFul<?> getChatRecord(Integer senderId, Integer receiverId) {
        Set<Object> history = new HashSet<>();
        String fromKey = "senderId:" + senderId + "receiverId:" + receiverId;
        String toKey = "senderId:" + receiverId + "receiverId:" + senderId;
        Optional<List<Object>> fromRedisData = Optional.ofNullable(redisUtils.lGet(fromKey, 0, -1));
        Optional<List<Object>> toRedisData = Optional.ofNullable(redisUtils.lGet(toKey, 0, -1));
        if (fromRedisData.isPresent()) {
            JSONArray from = JSONUtil.parseArray(JSONUtil.toJsonStr(fromRedisData.get()));
            if (0 < from.size()) {
                history.addAll(from);
            }
        }
        if (toRedisData.isPresent()) {
            JSONArray to = JSONUtil.parseArray(JSONUtil.toJsonStr(toRedisData.get()));
            if (0 < to.size()) {
                history.addAll(to);
            }
        }
        LambdaQueryWrapper<Message> messageLambdaQueryWrapper = Wrappers.lambdaQuery();
        messageLambdaQueryWrapper.eq(Message::getSenderId, senderId).eq(Message::getReceiverId, receiverId);
        Message fromMessage = messageMapper.selectOne(messageLambdaQueryWrapper);
        messageLambdaQueryWrapper.clear();
        messageLambdaQueryWrapper.eq(Message::getSenderId, receiverId).eq(Message::getReceiverId, senderId);
        Message toMessage = messageMapper.selectOne(messageLambdaQueryWrapper);

        if (!Objects.isNull(fromMessage)) {
            history.addAll(JSONUtil.parseArray(fromMessage.getContent()));
        }

        if (!Objects.isNull(toMessage)) {
            history.addAll(JSONUtil.parseArray(toMessage.getContent()));
        }
        List<Object> list = new ArrayList<>(List.copyOf(history));
        if (0 < list.size()) {
            list.sort((c1, c2) -> {
                Long l1 = JSONUtil.parseObj(c1).getLong("timeMillis");
                Long l2 = JSONUtil.parseObj(c2).getLong("timeMillis");
                return Long.compare(l1, l2);
            });
        }
        return ResultFul.success(BaseCode.SUCCESS, list);
    }
}
