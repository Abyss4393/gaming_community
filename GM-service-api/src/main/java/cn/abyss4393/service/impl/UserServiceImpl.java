package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.UserMapper;
import cn.abyss4393.po.User;
import cn.abyss4393.service.AbstractService;
import cn.abyss4393.service.IUserService;
import cn.abyss4393.utils.jwt.JwtUtils;
import cn.abyss4393.utils.redis.RedisUtils;
import cn.abyss4393.utils.timestamp.TimeStampUtil;
import cn.abyss4393.utils.wrap.WrapUtils;
import cn.abyss4393.vo.ResultVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.lettuce.core.RedisException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static cn.abyss4393.utils.rabbitmq.RabbitMQConstantUtils.*;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className UserService
 * @description TODO
 * @date 3/9/2023
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService, AbstractService<User> {

    @SuppressWarnings("all")
    @Autowired
    private UserMapper userMapper;

    @SuppressWarnings("all")
    @Autowired
    private RedisUtils redisUtils;


    @Override
    public ResultFul<?> login(User user) {
        if (StringUtils.isEmpty(user.getUsername())
                || StringUtils.isEmpty(user.getPassword()))
            return ResultFul.fail(BaseCode.MISS_PARAMS);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername, user.getUsername())
                .eq(User::getPassword, user.getPassword());
        User data = userMapper.selectOne(lambdaQueryWrapper);
        if (StringUtils.checkValNull(data))
            return ResultFul.fail(BaseCode.LOGIN_ERROR);
        data.setLastLandingTime(TimeStampUtil.getTimestamp());
        userMapper.updateById(data);
        String uid = String.valueOf(data.getId());
        String token = JwtUtils.createJWT(uid);
        redisUtils.set("token", token);
        redisUtils.set("用户ID:" + uid, data);
        redisUtils.expire("token", RedisUtils.HALF_HOUR);
        redisUtils.expire("用户ID:" + uid, RedisUtils.HALF_HOUR);
        ResultVo<Object> userVo = new ResultVo<>() {{
            this.setData(WrapUtils.removeAttr(data, "password"));
            this.setToken(token);
        }};
        return ResultFul.success(BaseCode.LOGIN_SUCCESS, userVo);
    }


    @Override
    public ResultFul<?> exit(Integer uid) {
        if (StringUtils.checkValNull(uid))
            return ResultFul.fail(BaseCode.ARGS_ERROR);
        if (redisUtils.get("token") == null || redisUtils.get("用户ID:" + uid) == null)
            return ResultFul.fail(BaseCode.LOGIN_NO);
        try {
            redisUtils.del("token", "用户ID:" + uid);
        } catch (RedisException e) {
            log.error("删除失败", e);
            return ResultFul.error(BaseCode.SYSTEM_ERROR);
        }
        return ResultFul.success(BaseCode.SUCCESS);
    }


    @Override
    public ResultFul<?> register(User user) {
        if (StringUtils.checkValNull(user))
            ResultFul.fail(BaseCode.ARGS_ERROR);
        User query = userMapper.selectOne(new LambdaQueryWrapper<>() {{
            this.eq(User::getUsername, user.getUsername());
            this.eq(User::getPassword, user.getPassword());
        }});
        if (StringUtils.checkValNotNull(query))
            return ResultFul.fail(BaseCode.REGISTER_REPETITION);
        user.setId(userMapper.getCount() + 1);
        user.setAvatar(User.DEFAULT_IMAGE);
        user.setNickname("用户" + (new Random().nextInt() * 1000));
        user.setCreateTime(TimeStampUtil.getTimestamp());
        user.setPermission(User.PERMISSION_OFFICIAL);
        return userMapper.insert(user) != 0 ?
                ResultFul.success(BaseCode.REGISTER_SUCCESS) :
                ResultFul.fail(BaseCode.REGISTER_ERROR);
    }


    public ResultFul<?> getUserInfoByUId(Integer uid) {
        if (StringUtils.checkValNull(uid)) return ResultFul.fail(BaseCode.SELECT_NULL_ARGS);
        User user = userMapper.selectOne(new LambdaQueryWrapper<>() {{
            this.eq(User::getId, uid);
        }});
        return ResultFul.success(BaseCode.SUCCESS,
                WrapUtils.removeAttr(user, "password"));
    }

    public ResultFul<?> update(User user) {
        Integer id = user.getId();
        if (StringUtils.checkValNull(id))
            return ResultFul.fail(BaseCode.ERROR);
        int update = userMapper.update(user, new LambdaQueryWrapper<>() {{
            this.eq(User::getId, id);
        }});
        return update != 0 ? ResultFul.success(BaseCode.SUCCESS) :
                ResultFul.fail(BaseCode.ERROR_REQUEST);
    }

    @Override
    public ResultFul<?> searchUser(String keyword, Integer pageNum, Integer pageSize) {
        Map<String, Object> searchPageResult = new HashMap<>();
        Page<User> searchPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(User::getUsername, keyword);
        queryWrapper.or().like(User::getNickname, keyword);
        queryWrapper.or().like(User::getEmail, keyword);
        Page<User> pages = userMapper.selectPage(searchPage, queryWrapper);
        searchPageResult.put("data", pages.getRecords());
        searchPageResult.put("currentPage", pages.getCurrent());
        searchPageResult.put("pageSize", pages.getSize());
        searchPageResult.put("total", pages.getTotal());
        return ResultFul.success(BaseCode.SUCCESS, searchPageResult);
    }

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Transactional
    @Override
    public ResultFul<?> modifyUser(User user) {
        if (Objects.isNull(user) || StringUtils.checkValNull(user.getId()))
            return ResultFul.fail(BaseCode.MODIFY_ERROR);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getId, user.getId());

        int update = userMapper.update(user, lambdaQueryWrapper);
        return 0 != update ? ResultFul.success(BaseCode.MODIFY) : ResultFul.fail(BaseCode.MODIFY_ERROR);
    }

    @Transactional
    @Override
    public ResultFul<?> deleteUser(Long id) {
        if (Objects.isNull(id)) return ResultFul.fail(BaseCode.DELETE_ERROR);
        int deleteRow = userMapper.deleteById(id);
        boolean sort = userMapper.sort();
        return 0 != deleteRow && sort ? ResultFul.success(BaseCode.DELETE) : ResultFul.fail(BaseCode.DELETE_ERROR);
    }
}
