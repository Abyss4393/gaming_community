package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.UserMapper;
import cn.abyss4393.po.BaseObj;
import cn.abyss4393.po.Manager;
import cn.abyss4393.po.User;
import cn.abyss4393.service.AbstractService;
import cn.abyss4393.utils.jwt.JwtUtils;
import cn.abyss4393.utils.redis.RedisUtils;
import cn.abyss4393.utils.timestamp.TimeStampUtil;
import cn.abyss4393.utils.wrap.WrapUtils;
import cn.abyss4393.vo.ResultVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.lettuce.core.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className UserService
 * @description TODO
 * @date 3/9/2023
 */
@Slf4j
@Service
public class UserServiceImpl implements AbstractService {

    @SuppressWarnings("all")
    @Autowired
    private UserMapper userMapper;

    @SuppressWarnings("all")
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public <T extends BaseObj> ResultFul<?> login(T t) {
        return null;
    }

    @Override
    public ResultFul<?> login(final User user) {
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
    public ResultFul<?> login(Manager manager) {
        return null;
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
    public <T extends BaseObj> ResultFul<?> register(T t) {
        return null;
    }

    @Override
    public ResultFul<?> register(Manager manager) {
        return null;
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


    @Override
    public boolean modify() {
        return false;
    }

    @Override
    public List<User> selectAll() {
        return userMapper.selectList(null);
    }


    @Override
    public boolean logout() {
        return false;
    }

    public ResultFul<?> getUserInfoByUId(Integer uid) {
        if (StringUtils.checkValNull(uid)) return ResultFul.fail(BaseCode.SELECT_NULL_ARGS);
        User user = userMapper.selectOne(new LambdaQueryWrapper<>() {{
            this.eq(User::getId, uid);
        }});
        return ResultFul.success(BaseCode.SUCCESS,
                WrapUtils.removeAttr(user, "password"));
    }
}
