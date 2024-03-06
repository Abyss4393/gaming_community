package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.ArticleMapper;
import cn.abyss4393.mapper.UserMapper;
import cn.abyss4393.po.Article;
import cn.abyss4393.po.User;
import cn.abyss4393.service.IAdminService;
import cn.abyss4393.utils.common.PageUtils;
import cn.abyss4393.utils.rabbitmq.RabbitMQConstantUtils;
import cn.abyss4393.vo.ArticleVo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className AdminServiceImpl
 * @description TODO
 * @date 2024/2/3
 * @completion false
 */
@Service
public class AdminServiceImpl implements IAdminService {

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private UserMapper userMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Override
    public ResultFul<?> selectBatchUsers(Integer currentPage, Integer pageSize) {
        if (null == currentPage || null == pageSize) return ResultFul.fail(BaseCode.ERROR);
        Page<User> pageUser = new Page<>(currentPage, pageSize);
        Page<User> userPage = userMapper.selectPage(pageUser, null);
        Map<String, Object> pageResult = PageUtils.createPageResultMap(userPage);
        return ResultFul.success(BaseCode.SUCCESS, pageResult);
    }

    @Override
    public ResultFul<?> selectBatchArticles(Integer currentPage, Integer pageSize) {
        if (null == currentPage || null == pageSize) return ResultFul.fail(BaseCode.ERROR);
        Page<Article> pageArticle = new Page<>(currentPage, pageSize);
        Page<Article> articlePage = articleMapper.selectPage(pageArticle, null);
        Map<String, Object> articlePageResult = PageUtils.createPageResultMap(articlePage);
        return ResultFul.success(BaseCode.SUCCESS, articlePageResult);
    }

    @Transactional
    @Override
    public ResultFul<?> confirmAuditArticle(Article article) {
        if (Objects.isNull(article)) return ResultFul.fail(BaseCode.ERROR);
        String msg = "你发布审核已通过，快去社区看看有人插眼没~";
        Map<String, Object> messageBodyForUser = RabbitMQConstantUtils.
                createMessageBodyForUser(article.getPosterId(), msg);
        article.setApproved(1);
        rabbitTemplate.convertAndSend(RabbitMQConstantUtils.DIRECT_EXCHANGE,
                RabbitMQConstantUtils.USER_QUEUE, messageBodyForUser);
        int update = articleMapper.updateById(article);
        return 0 != update ? ResultFul.success(BaseCode.SUCCESS) : ResultFul.fail(BaseCode.ERROR);
    }

    @Override
    public ResultFul<?> rejectAuditArticle(ArticleVo articleVo) {
        if (StringUtils.checkValNull(articleVo) || "".equals(articleVo.getNotificationContent()))
            return ResultFul.fail(BaseCode.ERROR);

        Article article = articleVo.getArticle();
        article.setApproved(-1);
        Map<String, Object> result = RabbitMQConstantUtils.createMessageBodyForUser(article.getPosterId(), articleVo.getNotificationContent());
        rabbitTemplate.convertAndSend(RabbitMQConstantUtils.DIRECT_EXCHANGE,
                RabbitMQConstantUtils.USER_QUEUE, result);
        int update = articleMapper.updateById(article);
        return 0 != update ? ResultFul.success(BaseCode.SUCCESS) : ResultFul.fail(BaseCode.ERROR);
    }

}
