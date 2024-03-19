package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.CommentMapper;
import cn.abyss4393.mapper.ReplyMapper;
import cn.abyss4393.mapper.UserMapper;
import cn.abyss4393.po.*;
import cn.abyss4393.service.IReplyService;
import cn.abyss4393.utils.file.FileUtils;
import cn.abyss4393.utils.rabbitmq.RabbitMQConstantUtils;
import cn.abyss4393.utils.timestamp.TimeStampUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;


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
    private RabbitTemplate rabbitTemplate;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private ReplyMapper replyMapper;
    @Resource
    private UserMapper userMapper;

    @Override
    public ResultFul<?> postReply(Reply reply) {
        List<Field> fields = Arrays.stream(reply.getClass().getDeclaredFields()).toList();
        if (fields.size() < 3) return ResultFul.fail(BaseCode.MISS_PARAMS);
        String replyContent = reply.getContent();
        if (!"".equals(replyContent) && replyContent.startsWith("<img")) {
            List<String> replacePaths = FileUtils.handlerBase64Content(replyContent);
            assert replacePaths != null;
            String replace = FileUtils.replace(replyContent, replacePaths);
            reply.setContent(replace);
        }
        LambdaQueryWrapper<Reply> existReplyWrapper = new LambdaQueryWrapper<>();
        existReplyWrapper.eq(Reply::getArticleId, reply.getArticleId());
        existReplyWrapper.eq(Reply::getUserId, reply.getUserId());
        existReplyWrapper.eq(Reply::getParentCommentId, reply.getParentCommentId());
        existReplyWrapper.eq(Reply::getParentReplyId, reply.getParentReplyId());
        boolean exists = replyMapper.exists(existReplyWrapper);
        if (!exists) {
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getId, reply.getUserId());
            User replier = userMapper.selectOne(userLambdaQueryWrapper);
            userLambdaQueryWrapper.clear();
            LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            commentLambdaQueryWrapper.eq(Comment::getId, reply.getParentCommentId());
            Comment comment = commentMapper.selectOne(commentLambdaQueryWrapper);
            userLambdaQueryWrapper.eq(User::getId, comment.getUId());
            User commenter = userMapper.selectOne(userLambdaQueryWrapper);
            final int columnCount = Math.toIntExact(replyMapper.selectCount(null) + 1);
            String timestamp = TimeStampUtil.getTimestamp();
            reply.setId(columnCount);
            Reply.init(reply);
            reply.setReplier(replier.getNickname());
            reply.setCommenter(commenter.getNickname());
            reply.setReplyTime(timestamp);
            int insert = replyMapper.insert(reply);
            String msg = "有新的回复发布，请及时处理";
            Map<String, Object> messageBodyForManager = RabbitMQConstantUtils.createMessageBodyForManager(msg,
                    String.valueOf(ManagerNotification.NOTIFICATION_TYPE.REPLY));
            rabbitTemplate.convertAndSend(RabbitMQConstantUtils.DIRECT_EXCHANGE,
                    RabbitMQConstantUtils.MANAGE_QUEUE, messageBodyForManager);
            return insert == 0 ? ResultFul.fail(BaseCode.REPLY_FAIL) :
                    ResultFul.success(BaseCode.REPLY_SUCCESS);
        }
        return ResultFul.fail(BaseCode.REPLY_FAIL);

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

    @Override
    public ResultFul<?> getBatchReplies(Integer currentPage, Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        Page<Reply> page = new Page<>(currentPage, pageSize);
        Page<Reply> replyPage = replyMapper.selectPage(page, null);
        result.put("currentPage", replyPage.getCurrent());
        result.put("pageSize", replyPage.getSize());
        result.put("data", replyPage.getRecords());
        result.put("total", replyPage.getTotal());
        return ResultFul.success(BaseCode.SUCCESS, result);
    }

    @Override
    public ResultFul<?> searchReply(String keyword, Integer currentPage, Integer pageSize) {
        Map<String, Object> searchResult = new HashMap<>();
        Page<Reply> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Reply> like = Wrappers.<Reply>lambdaQuery().
                like(Reply::getContent, keyword).
                or().like(Reply::getReplier, keyword).
                or().like(Reply::getCommenter, keyword).
                or().like(Reply::getParentCommentId, keyword);
        Page<Reply> replyPage = replyMapper.selectPage(page, like);
        searchResult.put("currentPage", replyPage.getCurrent());
        searchResult.put("pageSize", replyPage.getSize());
        searchResult.put("data", replyPage.getRecords());
        searchResult.put("total", replyPage.getTotal());
        return ResultFul.success(BaseCode.SUCCESS, searchResult);
    }

    @Transactional
    @Override
    public ResultFul<?> deleteReply(Serializable id) {
        if (StringUtils.checkValNull(id)) return ResultFul.fail(BaseCode.VALUE_NULL);
        LambdaQueryWrapper<Reply> existQueryWrapper = new LambdaQueryWrapper<>();
        existQueryWrapper.eq(Reply::getId, id);
        Reply reply = replyMapper.selectOne(existQueryWrapper);
        if (null == reply) return ResultFul.fail(BaseCode.DELETE_ERROR);
        int delete = replyMapper.delete(new LambdaQueryWrapper<Reply>().eq(Reply::getId, id));
        boolean sort = replyMapper.sort();
        return 0 != delete && sort ? ResultFul.success(BaseCode.DELETE) : ResultFul.fail(BaseCode.DELETE_ERROR);
    }

    @Override
    public ResultFul<?> like(Integer id, Integer from, Integer to) {
        LambdaQueryWrapper<Reply> replyLambdaQueryWrapper = Wrappers.lambdaQuery();
        replyLambdaQueryWrapper.eq(Reply::getId, id);
        Reply reply = replyMapper.selectOne(replyLambdaQueryWrapper);
        int like = reply.getReplyLike();
        like += 1;
        reply.setReplyLike(like);
        replyMapper.updateById(reply);
        User user = userMapper.getSimpleUserInfo(from);
        Map<String, User> userMap = Map.of("user", user);
        Map<String, Object> messageBodyForUser = RabbitMQConstantUtils.createMessageBodyForUser(to,
                String.valueOf(UserNotification.NOTIFICATION_TYPE.COMMENT), userMap.toString());
        rabbitTemplate.convertAndSend(RabbitMQConstantUtils.DIRECT_EXCHANGE, RabbitMQConstantUtils.USER_QUEUE, messageBodyForUser);
        return ResultFul.success(BaseCode.UPVOTE);
    }

    @Override
    public ResultFul<?> dislike(Integer id) {
        LambdaQueryWrapper<Reply> dislikeLambdaQueryWrapper = Wrappers.lambdaQuery();
        dislikeLambdaQueryWrapper.eq(Reply::getId, id);
        Reply reply = replyMapper.selectOne(dislikeLambdaQueryWrapper);
        int dislike = reply.getReplyDislike();
        dislike += 1;
        reply.setReplyLike(dislike);
        replyMapper.updateById(reply);
        return ResultFul.success(BaseCode.DISLIKE);
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
