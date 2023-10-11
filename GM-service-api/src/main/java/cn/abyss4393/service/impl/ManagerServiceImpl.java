package cn.abyss4393.service.impl;

import cn.abyss4393.entity.BaseCode;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.mapper.ManagerMapper;
import cn.abyss4393.po.BaseObj;
import cn.abyss4393.po.Manager;
import cn.abyss4393.po.User;
import cn.abyss4393.service.AbstractService;
import cn.abyss4393.utils.jwt.JwtUtils;
import cn.abyss4393.utils.redis.RedisUtils;
import cn.abyss4393.utils.timestamp.TimeStampUtil;
import cn.abyss4393.vo.ResultVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ManagerService
 * @description TODO
 * @date 3/9/2023
 */

@Service
public class ManagerServiceImpl implements AbstractService {

    @SuppressWarnings("all")
    @Autowired
    private ManagerMapper managerMapper;

    @SuppressWarnings("all")
    @Autowired
    private RedisUtils redisUtils;
    
    @Override
    public ResultFul<?> login(final Manager manager) {
        if (StringUtils.isEmpty(manager.getUsername())
                || StringUtils.isEmpty(manager.getPassword()))
            return ResultFul.fail(BaseCode.MISS_PARAMS);
        LambdaQueryWrapper<Manager> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Manager::getUsername, manager.getUsername())
                .eq(Manager::getPassword, manager.getPassword());
        Manager managerData = managerMapper.selectOne(lambdaQueryWrapper);
        if (StringUtils.checkValNull(managerData))
            return ResultFul.fail(BaseCode.LOGIN_ERROR);
        managerData.setLastLandingTime(TimeStampUtil.getTimestamp());
        managerMapper.updateById(managerData);
        String uid = String.valueOf(managerData.getId());
        String token = JwtUtils.createJWT(uid);
        redisUtils.set("token", token);
        redisUtils.set("用户ID:" + uid, managerData);
        redisUtils.expire("token", RedisUtils.HALF_HOUR);
        redisUtils.expire("用户ID:" + uid, RedisUtils.HALF_HOUR);
        ResultVo<Manager> managerResultVo = new ResultVo<>() {{
            this.setData(managerData);
            this.setToken(token);
        }};
        return ResultFul.success(BaseCode.LOGIN_SUCCESS, managerResultVo);
    }

    @Override
    public ResultFul<?> exit(Integer mid) {
        return null;
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
        return null;
    }


    @Override
    public boolean modify() {
        return false;
    }

    @Override
    public List<User> selectAll() {
        return null;
    }

    @Override
    public boolean logout() {
        return false;
    }

    @Override
    public <T extends BaseObj> ResultFul<?> login(T t) {
        return null;
    }

    @Override
    public ResultFul<?> login(User user) {
        return null;
    }
}
