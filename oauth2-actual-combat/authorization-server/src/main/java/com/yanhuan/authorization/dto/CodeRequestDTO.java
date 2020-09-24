package com.yanhuan.authorization.dto;

/**
 * code获取请求对象
 *
 * @author YanHuan
 * @date 2020-09-25 0:27
 */
public class CodeRequestDTO {

    private String appId;
    private String reqType;
    private String reqId;
    private String responseType;
    private String[] rScope;
    private String redirectUri;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getReqType() {
        return reqType;
    }

    public void setReqType(String reqType) {
        this.reqType = reqType;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String[] getrScope() {
        return rScope;
    }

    public void setrScope(String[] rScope) {
        this.rScope = rScope;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    @Override
    public String toString() {
        return "CodeRequestDTO{" +
                "reqType='" + reqType + '\'' +
                ", reqId='" + reqId + '\'' +
                ", responseType='" + responseType + '\'' +
                ", rScope='" + rScope + '\'' +
                '}';
    }
}
