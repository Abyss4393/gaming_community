package cn.abyss4393.interceptor;

import cn.abyss4393.annotation.AuthAccess;
import cn.abyss4393.po.User;
import cn.abyss4393.exception.ServiceException;
import cn.abyss4393.utils.jwt.JwtUtils;
import cn.abyss4393.utils.redis.RedisUtils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className JwtInterceptor
 * @description TODO
 * @date 3/9/2023
 */
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @SuppressWarnings("all")
    @Autowired
    private RedisUtils redisUtils;

    @SuppressWarnings("all")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            final AuthAccess methodAnnotation = ((HandlerMethod) handler).getMethodAnnotation(AuthAccess.class);
            if (methodAnnotation != null) {
                String info = methodAnnotation.desc();
                log.info("已放行" + info);
                return true;
            }
        }
        if (!(handler instanceof HandlerMethod)) return true;
        String token = request.getHeader("token");
        if (StringUtils.isBlank(token))
            token = request.getParameter("token");
        if (StringUtils.isBlank(token))
            throw new ServiceException("401", "请登录");
        String uid = null;
        try {
            uid = JwtUtils.parseJWT(token).getSubject();
        } catch (JWTDecodeException e) {
            throw new ServiceException("401", "请登录");
        }

        User userCache = (User) redisUtils.get("用户ID:" + uid);
        if (StringUtils.checkValNull(userCache))
            throw new ServiceException("401", "请登录");
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(userCache.getPassword())).build();
        try {
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new ServiceException("401", "请登录");
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
