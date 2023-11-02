package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.ArticleMapper;
import cn.abyss4393.mapper.CollectionMapper;
import cn.abyss4393.mapper.UserMapper;
import cn.abyss4393.po.Article;
import cn.abyss4393.po.Collection;
import cn.abyss4393.po.User;
import cn.abyss4393.service.IArticleService;
import cn.abyss4393.utils.timestamp.TimeStampUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.annotation.Resource;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
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
    private CollectionMapper collectionMapper;


    @Override
    public ResultFul<?> getArticleById(Serializable aid) throws Exception {
        Article article = articleMapper.selectById(aid);
        if (StringUtils.checkValNull(article))
            return ResultFul.fail(BaseCode.ARGS_ERROR);
        JSONObject temp = JSONUtil.parseObj(article);
        User tempUserInfo = userMapper.getSimpleUserInfo(Objects.requireNonNull((Integer) temp.get("posterId")));
        JSONObject tempContent = JSONUtil.parseObj(temp.getStr("content"));
        temp.set("posterData", tempUserInfo);
        temp.replace("content", tempContent);
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

    @Override
    public ResultFul<?> postArticle(Article article) {
        if (StringUtils.checkValNull(article))
            return ResultFul.fail(BaseCode.ARGS_ERROR);
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Article::getId, article.getId());
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

    @Override
    public ResultFul<?> addPositivenessCount(@NonNull Integer aid) {
        if (StringUtils.checkValNull(aid))
            return ResultFul.fail(BaseCode.ARGS_ERROR);
        int count = articleMapper.getPositivenessCount(aid);
        AtomicInteger atomicInteger = new AtomicInteger(count);
        atomicInteger.incrementAndGet();
        int update = articleMapper.update(new Article() {{
            this.setId(aid);
            this.setPositivenessCount(atomicInteger.get());
        }}, new LambdaQueryWrapper<>() {{
            this.eq(Article::getId, aid);
        }});
        if (update == 0) {
            ResultFul.fail(BaseCode.UPVOTE_FAIL);
        }
        return ResultFul.success(BaseCode.UPVOTE);
    }

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
                if (0 != update && insert != 0)
                    return ResultFul.success(BaseCode.COLLECT);
            }
        }

        return ResultFul.fail(BaseCode.COLLECT_FAIL);
    }
}
