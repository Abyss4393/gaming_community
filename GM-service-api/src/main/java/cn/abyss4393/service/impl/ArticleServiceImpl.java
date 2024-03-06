package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.*;
import cn.abyss4393.po.Collection;
import cn.abyss4393.po.*;
import cn.abyss4393.service.IArticleService;
import cn.abyss4393.utils.file.FileUtils;
import cn.abyss4393.utils.timestamp.TimeStampUtil;
import cn.abyss4393.vo.CommentVo;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ArticleServiceImpl
 * @description TODO
 * @date 21/9/2023
 */
@Service
public class ArticleServiceImpl implements IArticleService {


    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UpvoteMapper upvoteMapper;

    @Resource
    private CollectionMapper collectionMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private ReplyMapper replyMapper;

    @Override
    public ResultFul<?> getArticleById(Serializable aid) throws Exception {
        Article article = articleMapper.selectById(aid);
        if (StringUtils.checkValNull(article))
            return ResultFul.fail(BaseCode.ARGS_ERROR);
        JSONObject temp = JSONUtil.parseObj(article);
        User tempUserInfo = userMapper.getSimpleUserInfo(Objects.requireNonNull((Integer) temp.get("posterId")));
        tempUserInfo.setId(temp.getInt("posterId"));
        List<CommentVo> commentVos = new ArrayList<>();
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Comment::getAId, aid);
        final List<Comment> list = commentMapper.selectList(lambdaQueryWrapper);
        list.forEach(item -> {
            CommentVo commentvo = new CommentVo();
            commentvo.setComment(item);
            User user = userMapper.getSimpleUserInfo(item.getUId());
            user.setId(item.getUId());
            commentvo.setUser(user);
            commentVos.add(commentvo);
        });
        JSONObject tempContent = JSONUtil.parseObj(temp.getStr("content"));
        temp.set("posterData", tempUserInfo);
        temp.replace("content", tempContent);
        temp.set("comments", commentVos);
        return ResultFul.success(BaseCode.SUCCESS, temp);
    }

    @Override
    public ResultFul<?> getArticleListByPid(Serializable pid) throws Exception {
        return ResultFul.success(BaseCode.SUCCESS,articleMapper.selectList(new LambdaQueryWrapper<>(){{
            this.eq(Article::getPosterId,pid);
        }}));
    }

    @Override
    public ResultFul<?> getArticleList() throws Exception {
        JSONArray ja = JSONUtil.parseArray(Objects.requireNonNull(articleMapper.selectList(null)));
        JSONArray handlerArray = new JSONArray();
        ja.forEach(item -> {
            JSONObject temp = JSONUtil.parseObj(item);
            User tempUser = userMapper.getSimpleUserInfo(Objects.requireNonNull((Integer) temp.get("posterId")));
            JSONObject tempContent = JSONUtil.parseObj(temp.getStr("content"));
            temp.replace("content", tempContent);
            temp.set("posterData", tempUser);
            handlerArray.add(temp);
        });

        return StringUtils.checkValNotNull(handlerArray) ?
                ResultFul.success(BaseCode.SUCCESS, handlerArray) :
                ResultFul.fail(BaseCode.ERROR, null);


    }

    @Transactional
    @Override
    public ResultFul<?> postArticle(Article article) {
        if (StringUtils.checkValNull(article))
            return ResultFul.fail(BaseCode.ARGS_ERROR);
        JSONObject frontObjData = JSONUtil.parseObj(article.getContent());
        JSONArray frontJSONData = frontObjData.getJSONArray("contentList");
        List<String> replaces = new ArrayList<>();
        frontJSONData.forEach(item -> {
            JSONObject temp = JSONUtil.parseObj(item);
            String textContent = temp.getStr("text");
            if ("".equals(textContent) || textContent.contains("<img")) {
                List<String> replacePaths = FileUtils.handlerBase64Content(textContent);
                assert replacePaths != null;
                String replace = FileUtils.replace(textContent, replacePaths);
                replaces.add(replace);
            }
        });
        for (int i = 0; i < replaces.size(); i++) {
            JSONObject tempObj = JSONUtil.parseObj(frontJSONData.get(i));
            tempObj.replace("text", replaces.get(i));
            frontJSONData.set(i, tempObj);
        }
        article.setContent(JSONUtil.toJsonStr(frontObjData));
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Article::getPosterId, article.getPosterId());
        lambdaQueryWrapper.eq(Article::getTitle, article.getTitle());
        boolean exists = articleMapper.exists(lambdaQueryWrapper);
        if (!exists) {
            article.setId(Math.toIntExact(articleMapper.selectCount(null)) + 1);
            article.setPostTime(TimeStampUtil.getTimestamp());
            Article.initDefaultAttr(article);
            articleMapper.insert(article);
            return ResultFul.success(BaseCode.POST);
        }
        return ResultFul.fail(BaseCode.POST_FAIL);
    }

    @Transactional
    @Override
    public ResultFul<?> addPositivenessCount(Integer uid, Integer aid) {
        if (StringUtils.checkValNull(aid))
            return ResultFul.fail(BaseCode.ARGS_ERROR);
        int count = articleMapper.getPositivenessCount(aid);
        AtomicInteger atomicInteger = new AtomicInteger(count);
        atomicInteger.incrementAndGet();
        final int update = articleMapper.update(new Article() {{
            this.setId(aid);
            this.setPositivenessCount(atomicInteger.get());
        }}, new LambdaQueryWrapper<>() {{
            this.eq(Article::getId, aid);
        }});
        LambdaQueryWrapper<Upvote> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Upvote::getUId, uid);
        lambdaQueryWrapper.eq(Upvote::getAId, aid);
        final boolean exists = upvoteMapper.exists(lambdaQueryWrapper);
        if (!exists) {
            Upvote upvote = new Upvote();
            upvote.setId(Math.toIntExact((upvoteMapper.selectCount(null) + 1)));
            upvote.setUId(uid);
            upvote.setAId(aid);
            final int insert = upvoteMapper.insert(upvote);
            if (update == 0 || insert == 0) {
                ResultFul.fail(BaseCode.UPVOTE_FAIL);
            }
        }
        return ResultFul.fail(BaseCode.UPVOTE_FAIL);
    }

    @Transactional
    @Override
    public ResultFul<?> addPassivenessCount(@NonNull Integer aid) {
        if (StringUtils.checkValNull(aid))
            return ResultFul.fail(BaseCode.ARGS_ERROR);
        int count = articleMapper.getPassivenessCount(aid);
        AtomicInteger atomicInteger = new AtomicInteger(count);
        atomicInteger.incrementAndGet();

        int update = articleMapper.update(new Article() {{
            this.setId(aid);
            this.setPassivenessCount(atomicInteger.get());
        }}, new LambdaQueryWrapper<>() {{
            this.eq(Article::getId, aid);
        }});
        if (update == 0) {
            ResultFul.fail(BaseCode.DISLIKE_FAIL);
        }
        return ResultFul.success(BaseCode.DISLIKE);
    }

    @Transactional
    @Override
    public ResultFul<?> addCollect(Integer id, Integer uid) {
        if (StringUtils.checkValNull(id) || StringUtils.checkValNull(uid))
            return ResultFul.fail(BaseCode.ARGS_ERROR);
        LambdaQueryWrapper<Collection> collectionLambdaQueryWrapper =
                new LambdaQueryWrapper<>();
        collectionLambdaQueryWrapper.eq(Collection::getUserId, uid);
        collectionLambdaQueryWrapper.eq(Collection::getArticleId, id);
        boolean collectionExits = collectionMapper.exists(collectionLambdaQueryWrapper);
        if (!collectionExits) {
            int insert = collectionMapper.insert(new Collection() {{
                this.setId(Math.toIntExact(collectionMapper.selectCount(null)) + 1);
                this.setUserId(uid);
                this.setArticleId(id);
            }});
            LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Article::getId, id);
            boolean exists = articleMapper.exists(wrapper);
            if (exists) {
                AtomicInteger atomicInteger =
                        new AtomicInteger(articleMapper.getCollectCount(id));
                atomicInteger.incrementAndGet();
                int update = articleMapper.update(new Article() {{
                    this.setId(id);
                    this.setPosterId(uid);
                    this.setCollectCount(atomicInteger.get());
                }}, new LambdaQueryWrapper<>() {{
                    this.eq(Article::getId, id);
                }});
                if (0 != update && 0 != insert)
                    return ResultFul.success(BaseCode.COLLECT);
            }
        }

        return ResultFul.fail(BaseCode.COLLECT_FAIL);
    }

    @Override
    public ResultFul<?> searchArticles(String keyword, Integer currentPage, Integer pageSize) {
        Page<Article> pageArticle = new Page<>(currentPage, pageSize);
        Map<String, Object> articlePageResult = new HashMap<>();
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Article::getTitle, keyword).
                or().like(Article::getType, keyword).
                or().like(Article::getPosterName, keyword).
                or().like(Article::getContentDes, keyword);
        Page<Article> articlePage = articleMapper.selectPage(pageArticle, queryWrapper);
        articlePageResult.put("currentPage", currentPage);
        articlePageResult.put("pageSize", pageSize);
        articlePageResult.put("total", articlePage.getTotal());
        articlePageResult.put("data", articlePage.getRecords());
        return ResultFul.success(BaseCode.SUCCESS, articlePageResult);
    }

    @Override
    public ResultFul<?> findUnapprovedArticles(Integer currentPage, Integer pageSize) {
        Page<Article> unapprovedPage = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Article> lq = new LambdaQueryWrapper<>();
        lq.eq(Article::getApproved, 0);
        Page<Article> unapprovedList = articleMapper.selectPage(unapprovedPage, lq);
        Map<String, Object> result = new HashMap<>();
        result.put("currentPage", currentPage);
        result.put("pageSize", pageSize);
        result.put("total", unapprovedList.getTotal());
        result.put("data", unapprovedList.getRecords());
        return ResultFul.success(BaseCode.SUCCESS, result);
    }

    @Transactional
    @Override
    public ResultFul<?> deleteArticleById(Serializable id) throws Exception {
        if (Objects.isNull(id)) return ResultFul.fail(BaseCode.DELETE_ERROR);
        int deleteUpvote = upvoteMapper.delete(new LambdaQueryWrapper<>() {{
            this.eq(Upvote::getAId, id);
        }});
        int deleteCollect = collectionMapper.delete(new LambdaQueryWrapper<>() {{
            this.eq(Collection::getArticleId, id);
        }});
        int deleteComment = commentMapper.delete(new LambdaQueryWrapper<>() {{
            this.eq(Comment::getAId, id);
        }});

        int delete = replyMapper.delete(new LambdaQueryWrapper<>() {{
            this.eq(Reply::getArticleId, id);
        }});

        int deleteArticle = articleMapper.deleteById(id);
        boolean sort = articleMapper.sort();
        return 0 != deleteArticle && sort ? ResultFul.success(BaseCode.DELETE) : ResultFul.fail(BaseCode.DELETE_ERROR);
    }
}
