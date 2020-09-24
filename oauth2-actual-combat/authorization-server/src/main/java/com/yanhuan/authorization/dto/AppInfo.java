package com.yanhuan.authorization.dto;

/**
 * 模拟  初始化  第三方软件注册信息
 *
 * @author YanHuan
 * @date 2020-09-24 0:03
 */
public class AppInfo {

    public static final String APP_ID = "APPID_RABBIT";
    public static final String APP_SECRET = "APPSECRET_RABBIT";
    /**
     * 重定向地址
     */
    public static final String REDIRECT_URI = "http://localhost:8080/AppServlet-ch03";

    /**
     * 权限范围
     */
    public static final String SCOPE = "today history";
}
