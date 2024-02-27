package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.ArticleMapper;
import cn.abyss4393.mapper.UserMapper;
import cn.abyss4393.po.Article;
import cn.abyss4393.po.User;
import cn.abyss4393.service.IAdminService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private UserMapper userMapper;

    @Resource
    private ArticleMapper articleMapper;


    @Override
    public ResultFul<?> selectBatchUsers(Integer currentPage, Integer pageSize) {
        if (currentPage == null || pageSize == null) return ResultFul.fail(BaseCode.ERROR);
        Page<User> pageUser = new Page<>(currentPage,pageSize);
        Page<User> userPage = userMapper.selectPage(pageUser, null);
        long total = userPage.getTotal();
        List<User> users = userPage.getRecords();
        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("data", users);
        pageResult.put("currentPage", currentPage);
        pageResult.put("pageSize", pageSize);
        pageResult.put("total", total);
        return ResultFul.success(BaseCode.SUCCESS, pageResult);
    }

    @Override
    public ResultFul<?> selectBatchArticles(Integer currentPage, Integer pageSize) {
        if (currentPage == null || pageSize == null) return ResultFul.fail(BaseCode.ERROR);
        Map<String, Object> articlePageResult = new HashMap<>();
        Page<Article> pageArticle = new Page<>(currentPage,pageSize);
        Page<Article> articlePage = articleMapper.selectPage(pageArticle, null);
        articlePageResult.put("data",articlePage.getRecords());
        articlePageResult.put("currentPage",currentPage);
        articlePageResult.put("pageSize",pageSize);
        articlePageResult.put("total",articlePage.getTotal());
        return ResultFul.success(BaseCode.SUCCESS, articlePageResult);
    }
}
