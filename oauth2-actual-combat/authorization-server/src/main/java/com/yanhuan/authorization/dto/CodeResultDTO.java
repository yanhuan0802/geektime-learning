package com.yanhuan.authorization.dto;

/**
 * 授权码返回
 *
 * @author YanHuan
 * @date 2020-09-25 0:45
 */
public class CodeResultDTO {

    private String code;
    private String toAppUrl;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getToAppUrl() {
        return toAppUrl;
    }

    public void setToAppUrl(String toAppUrl) {
        this.toAppUrl = toAppUrl;
    }

    @Override
    public String toString() {
        return "CodeResultDTO{" +
                "code='" + code + '\'' +
                ", toAppUrl='" + toAppUrl + '\'' +
                '}';
    }
}
