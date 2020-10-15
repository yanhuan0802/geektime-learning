package com.yanhuan.authorization.util;

import com.yanhuan.authorization.dto.AppInfo;
import io.jsonwebtoken.*;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * 工具类
 *
 * @author YanHuan
 * @date 2020-09-24 23:45
 */
public class AuthUtil {

    /**
     * 模拟授权码、令牌等数据存储
     */
    public static Map<String, String> codeMap = new HashMap<>();

    public static Map<String, String> tokenMap = new HashMap<>();

    public static Map<String, String> refreshTokenMap = new HashMap<>();

    /**
     * 生成code值
     *
     * @return code值
     */
    public static String generateCode(String appId, String user) {
        Random r = new Random();
        StringBuilder strb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            strb.append(r.nextInt(10));
        }

        String code = strb.toString();


        //在这一篇章我们仅作为演示用，实际这应该是一个全局内存数据库，有效期官方建议是10分钟
        codeMap.put(code, appId + "|" + user + "|" + System.currentTimeMillis());

        return code;
    }


    /**
     * 生成access_token值
     *
     * @param appId 应用ID
     * @param user  用户信息
     * @return token
     */
    public static String generateAccessToken(String appId, String user) {

        String sharedTokenSecret = UUID.randomUUID().toString();

        Key key = new SecretKeySpec(sharedTokenSecret.getBytes(), SignatureAlgorithm.HS256.getJcaName());

        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("typ", "JWT");
        headerMap.put("alg", "HS256");

        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("iss", "http://localhost:8081/");
        payloadMap.put("sub", "XIAOMINGTEST");
        payloadMap.put("aud", "APPID_RABBIT");
        payloadMap.put("exp", 1584105790703L);
        payloadMap.put("iat", 1584105948372L);
        //生成JWT令牌
        String jwts = Jwts.builder()
                .setHeaderParams(headerMap)
                .setClaims(payloadMap)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        //1天时间过期
        String expiresIn = "1";

        //仅作为演示用，实际这应该是一个全局数据库,并且有有效期
        tokenMap.put(jwts, appId + "|" + user + "|" + System.currentTimeMillis() + "|" + expiresIn);

        //解析JWT令牌
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(jwts);
        JwsHeader header = claimsJws.getHeader();
        Claims body = claimsJws.getBody();
        //解析时错误说明：JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.
        return jwts;
    }


    /**
     * 生成refresh_token值
     *
     * @param appId 应用ID
     * @param user  用户信息
     * @return 刷新令牌
     */
    public static String generateRefreshToken(String appId, String user) {

        String refreshToken = UUID.randomUUID().toString();

        //仅作为演示用，实际这应该是一个全局数据库,并且有有效期
        refreshTokenMap.put(refreshToken, appId + "|" + user + "|" + System.currentTimeMillis());

        return refreshToken;
    }

    /**
     * 是否存在code值
     *
     * @param code code
     * @return 验证结果
     */
    public static boolean isExistCode(String code) {
        return codeMap.containsKey(code);
    }

    /**
     * 验证权限
     *
     * @param scope 权限范围
     * @return 验证结果
     */
    public static boolean checkScope(String scope) {
        //简单模拟权限验证
        return AppInfo.SCOPE.contains(scope);
    }


    /**
     * 验证权限
     *
     * @param rscope 权限范围
     * @return 验证结果
     */
    public static boolean checkScope(String[] rscope) {
        StringBuilder scope = new StringBuilder();
        for (String s : rscope) {
            scope.append(s);
        }
        //简单模拟权限验证
        return AppInfo.SCOPE.replace(" ", "").contains(scope.toString());
    }
}
