package com.yanhuan.authorization.dto;

/**
 * Token请求参数
 *
 * @author YanHuan
 * @date 2020-10-14 23:07
 */
public class TokenRequestDTO {

    /**
     * 类型
     */
    private String grantType;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 应用秘钥
     */
    private String appSecret;

    /**
     * 授权码
     */
    private String code;

    private String refreshToken;


    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "TokenRequestDTO{" +
                "grantType='" + grantType + '\'' +
                ", appId='" + appId + '\'' +
                ", appSecret='" + appSecret + '\'' +
                ", code='" + code + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
