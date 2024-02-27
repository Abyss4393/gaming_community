package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.ReplyMapper;
import cn.abyss4393.mapper.UserMapper;
import cn.abyss4393.po.Reply;
import cn.abyss4393.po.User;
import cn.abyss4393.service.IReplyService;
import cn.abyss4393.utils.timestamp.TimeStampUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ReplyServiceImpl
 * @description TODO
 * @date 26/11/2023
 * @completion false
 */
@Service
public class ReplyServiceImpl implements IReplyService {

    @Resource
    private ReplyMapper replyMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public ResultFul<?> postReply(Reply reply) {
        int insert = 0;
        final List<Field> fields = Arrays.stream(reply.getClass().getDeclaredFields()).toList();
        if (fields.size() < 3) return ResultFul.fail(BaseCode.MISS_PARAMS);
        LambdaQueryWrapper<Reply> existReplyWrapper = new LambdaQueryWrapper<>();
        existReplyWrapper.eq(Reply::getArticleId, reply.getArticleId());
        existReplyWrapper.eq(Reply::getUserId, reply.getUserId());
        existReplyWrapper.eq(Reply::getParentCommentId, reply.getParentCommentId());
        existReplyWrapper.eq(Reply::getParentReplyId, reply.getParentReplyId());
        final boolean exists = replyMapper.exists(existReplyWrapper);
        System.out.println(reply);
        System.out.println(exists);
        if (!exists) {
            final int columnCount = Math.toIntExact(replyMapper.selectCount(null) + 1);
            String timestamp = TimeStampUtil.getTimestamp();
            reply.setId(columnCount);
            reply.setReplyTime(timestamp);
            insert = replyMapper.insert(reply);
        }
        return insert == 0 ? ResultFul.fail(BaseCode.REPLY_FAIL) :
                ResultFul.success(BaseCode.REPLY_SUCCESS);
    }

    @Override
    public ResultFul<?> getRepliesByUId(Integer uid) {
        if (StringUtils.checkValNull(uid))
            return ResultFul.fail(BaseCode.MISS_PARAMS);
        LambdaQueryWrapper<Reply> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Reply::getUserId, uid);
        lambdaQueryWrapper.orderByDesc(Reply::getUserId);
        final List<Reply> replies = replyMapper.selectList(lambdaQueryWrapper);
        return ResultFul.success(BaseCode.SUCCESS, replies);
    }

    @Override
    public ResultFul<?> getRepliesTreeByUID(Integer aid, Integer commentId, Integer replyId) {
        if (StringUtils.checkValNull(aid))
            return ResultFul.fail(BaseCode.MISS_PARAMS);
        LambdaQueryWrapper<Reply> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Reply::getArticleId, aid);
        lambdaQueryWrapper.eq(Reply::getParentCommentId, commentId);
        final List<Reply> replies = replyMapper.selectList(lambdaQueryWrapper);
        JSONObject result = new JSONObject();
        List<JSONObject> jsonArray = new ArrayList<>();
        for (Reply reply : replies) {
            JSONObject jsonObject = JSONUtil.parseObj(reply);
            final Integer userId = reply.getUserId();
            final User simpleUserInfo = userMapper.getSimpleUserInfo(userId);
            jsonObject.set("user", simpleUserInfo);
            jsonArray.add(jsonObject);
        }
//        recursion(jsonArray, replyId);
        result.set("primary", jsonArray);
        if (jsonArray.size() == 0) result.replace("primary", null);
        return ResultFul.success(BaseCode.SUCCESS, result);
    }

    private void recursion(List<JSONObject> jsonObjectList, Integer replyId) {
        for (JSONObject jsonObject : jsonObjectList) {
            LambdaQueryWrapper<Reply> conditionQueryWrapper = new LambdaQueryWrapper<>();
            conditionQueryWrapper.or().eq(Reply::getParentReplyId, replyId);
            final List<Reply> replies = replyMapper.selectList(conditionQueryWrapper);
            if (replies.size() == 0) return;
            JSONArray array = new JSONArray();
            for (Reply reply : replies) {
                JSONObject secondaryObj = JSONUtil.parseObj(reply);
                final int tempUserId = reply.getUserId();
                final User simpleUserInfo = userMapper.getSimpleUserInfo(tempUserId);
                secondaryObj.set("user", simpleUserInfo);
                array.add(secondaryObj);
            }
            jsonObject.set("secondary", array);
        }
        recursion(jsonObjectList, replyId);
    }
}
