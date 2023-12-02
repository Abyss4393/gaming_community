package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.service.IHistoryService;
import cn.abyss4393.utils.redis.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className HistoryServiceImpl
 * @description TODO
 * @date 29/11/2023
 * @completion false
 */
@Service
public class HistoryServiceImpl implements IHistoryService {

    @Resource
    private RedisUtils redisUtils;

    @Override
    public ResultFul<?> loadHistory() {
        final Optional<List<String>> query_history =  Optional.ofNullable((List<String>) redisUtils.get("query_history"));
        return ResultFul.success(BaseCode.SUCCESS,query_history.orElse(new ArrayList<>(){{
            this.add("暂无历史记录");
        }}));
    }

    @Override
    public ResultFul<?> saveHistory(String keyword) {
        return null;
    }
}
