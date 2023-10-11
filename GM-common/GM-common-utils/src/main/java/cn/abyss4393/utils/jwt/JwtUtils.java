package cn.abyss4393.utils.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className JwtUtils
 * @description TODO
 * @date 2/9/2023
 */


public class JwtUtils {


    //TODO 设置过期时间
    private static long JWT_EXPIRATION = 60 * 30L;
    //TODO key的长度必须为4的倍数
    private static final String JWT_KEY = "GAMECOMMUNITY789";

    public void setJwtExpiration(long expiration) {
        if (expiration > 60)
            JWT_EXPIRATION = expiration;
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * 生成一个JWT密钥
     *
     * @param subject 选择加密的中间件
     * @return 字符串密钥
     */
    public static String createJWT(String subject) {
        return getJWTBuilder(subject, null, generateUUID()).compact();
    }

    /**
     * 生成一个JWT密钥
     *
     * @param subject 选择加密的中间件
     * @param mills 密钥失效时间 - 毫秒
     * @return 字符串密钥
     */
    public static String createJWT(String subject, Long mills) {
        return getJWTBuilder(subject, mills, generateUUID()).compact();
    }

    private static JwtBuilder getJWTBuilder(String subject, Long mills, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generateKey();
        long nowMills = System.currentTimeMillis();
        Date nowDate = new Date(nowMills);
        if (mills == null) {
            mills = JwtUtils.JWT_EXPIRATION;
        }
        long expireMills = nowMills + mills;
        Date expDate = new Date(expireMills);
        return Jwts.builder()
                .setId(uuid) //唯一id
                .setSubject(subject) //主题
                .setIssuer("abyss") //签发者
                .setIssuedAt(nowDate) //签发时间
                .signWith(signatureAlgorithm, secretKey)
                .setExpiration(expDate);

    }


    /**
     * 解析密钥
     *
     * @return Claims
     */
    public static Claims parseJWT(String jwt) {
        return Jwts.parser()
                .setSigningKey(generateKey())
                .parseClaimsJws(jwt)
                .getBody();
    }

    /**
     * 生成密钥
     *
     * @return SecretKey
     */
    private static SecretKey generateKey() {
        byte[] encodeKey = Base64.getDecoder().decode(JwtUtils.JWT_KEY);
        return new SecretKeySpec(encodeKey, 0, encodeKey.length, "AES");
    }

}
