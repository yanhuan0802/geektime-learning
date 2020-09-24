package com.yanhuan.authorization.util;

import com.yanhuan.authorization.dto.AppInfo;

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

    //模拟授权码、令牌等数据存储
    static Map<String,String> codeMap =  new HashMap<>();

    static Map<String,String> tokenMap =  new HashMap<>();

    static Map<String,String> refreshTokenMap =  new HashMap<>();

    /**
     * 生成code值
     *
     * @return code值
     */
    public static  String generateCode(String appId, String user) {
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
     * @param appId
     * @param user
     * @return
     */
    public static  String generateAccessToken(String appId, String user) {

        String accessToken = UUID.randomUUID().toString();

        String expires_in = "1";//1天时间过期

        tokenMap.put(accessToken, appId + "|" + user + "|" + System.currentTimeMillis() + "|" + expires_in);//在这一篇章我们仅作为演示用，实际这应该是一个全局数据库,并且有有效期

        return accessToken;
    }


    /**
     * 生成refresh_token值
     *
     * @param appId
     * @param user
     * @return
     */
    public static  String generateRefreshToken(String appId, String user) {

        String refreshToken = UUID.randomUUID().toString();

        refreshTokenMap.put(refreshToken, appId + "|" + user + "|" + System.currentTimeMillis());//在这一篇章我们仅作为演示用，实际这应该是一个全局数据库,并且有有效期

        return refreshToken;
    }

    /**
     * 是否存在code值
     *
     * @param code
     * @return
     */
    public static  boolean isExistCode(String code) {
        return codeMap.containsKey(code);
    }

    /**
     * 验证权限
     *
     * @param scope
     * @return
     */
    public static  boolean checkScope(String scope) {
        //简单模拟权限验证
        return AppInfo.SCOPE.contains(scope);
    }


    /**
     * @param rscope
     * @return
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
