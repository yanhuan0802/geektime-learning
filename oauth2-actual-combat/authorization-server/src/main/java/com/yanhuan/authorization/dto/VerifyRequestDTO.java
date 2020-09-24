package com.yanhuan.authorization.dto;

/**
 * 第三方信息验证 请求DTO
 *
 * @author YanHuan
 * @date 2020-09-24 23:37
 */
public class VerifyRequestDTO {
    private String responseType;
    private String redirectUri;
    private String appId;
    private String scope;

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "CodeRequestDTO{" +
                "responseType='" + responseType + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                ", appId='" + appId + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}
