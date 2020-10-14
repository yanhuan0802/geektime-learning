package com.yanhuan.authorization.controller;

import com.yanhuan.authorization.constant.GrantTypeConstant;
import com.yanhuan.authorization.dto.*;
import com.yanhuan.authorization.util.AuthUtil;
import com.yanhuan.authorization.util.URLParamsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;


/**
 * 授权controller
 *
 * @author YanHuan
 * @date 2020-09-24 23:34
 */
@Controller
public class AuthorizationController {

    static Map<String, String> reqidMap = new HashMap<>();

    static Map<String, String> refreshAccessMap = new HashMap<>();

    static Map<String, String[]> codeScopeMap = new HashMap<>();

    static Map<String, String[]> tokenScopeMap = new HashMap<>();


    /**
     * 验证客户端基本信息
     *
     * @param verifyRequestDTO 验证请求参数
     */
    @ResponseBody
    @PostMapping("/verify")
    public VerifyResultDTO verify(@RequestBody VerifyRequestDTO verifyRequestDTO) {
        if (AppInfo.APP_ID.equals(verifyRequestDTO.getAppId())) {
            return null;
        }
        if (AppInfo.REDIRECT_URI.equals(verifyRequestDTO.getRedirectUri())) {
            return null;
        }
        //验证第三方软件请求的权限范围是否与当时注册的权限范围一致
        if (!AuthUtil.checkScope(verifyRequestDTO.getScope())) {
            //超出注册的权限范围
            return null;
        }

        //生成页面reqid
        String reqid = String.valueOf(System.currentTimeMillis());
        //保存该reqid值
        reqidMap.put(reqid, reqid);

        VerifyResultDTO verifyResultDTO = new VerifyResultDTO();
        verifyResultDTO.setAppId(verifyRequestDTO.getAppId());
        verifyResultDTO.setRedirectUri(verifyRequestDTO.getRedirectUri());
        verifyResultDTO.setResponseType(verifyRequestDTO.getResponseType());
        verifyResultDTO.setReqId(reqid);

        //至此颁发授权码code的准备工作完毕
        return verifyResultDTO;
    }


    private static final String APPROVE = "approve";
    private static final String CODE = "code";

    /**
     * 获取授权码Code
     *
     * @param codeRequestDTO 验证请求参数
     */
    @ResponseBody
    @PostMapping("/code")
    public String getCode(@RequestBody CodeRequestDTO codeRequestDTO) {
        //处理用户点击approve按钮动作
        if (APPROVE.equals(codeRequestDTO.getReqType())) {
            //假设不为空
            if (!reqidMap.containsKey(codeRequestDTO.getReqId())) {
                return null;
            }

            if (CODE.equals(codeRequestDTO.getResponseType())) {
                //二次验证权限范围
                if (!AuthUtil.checkScope(codeRequestDTO.getrScope())) {
                    //超出注册的权限范围
                    System.out.println("out of scope ...");
                    return null;
                }

                //模拟登陆用户为USERTEST生成code   code设置有效期
                String code = AuthUtil.generateCode(codeRequestDTO.getAppId(), "USERTEST");
                //授权范围与授权码做绑定
                codeScopeMap.put(code, codeRequestDTO.getrScope());
                Map<String, String> params = new HashMap<>();
                params.put("code", code);

                //构造第三方软件的回调地址，并重定向到该地址
                String toAppUrl = URLParamsUtil.appendParams(codeRequestDTO.getRedirectUri(), params);

                //授权码流程的【第二次】重定向
                CodeResultDTO codeResultDTO = new CodeResultDTO();
                codeResultDTO.setCode(code);
                codeResultDTO.setToAppUrl(toAppUrl);
                return toAppUrl;
            }
        }
        return null;
    }


    /**
     * 获取访问令牌
     *
     * @param tokenRequestDTO 令牌获取
     */
    @ResponseBody
    @PostMapping("/token")
    public String getToken(@RequestBody TokenRequestDTO tokenRequestDTO) {
        if (!AppInfo.APP_ID.equals(tokenRequestDTO.getAppId())) {
            return "app_id is not available";
        }
        if (!AppInfo.APP_SECRET.equals(tokenRequestDTO.getAppSecret())) {
            return "app_secret is not available";
        }
        //处理授权码流程中的 颁发访问令牌 环节
        if (GrantTypeConstant.AUTHORIZATION_CODE.equals(tokenRequestDTO.getGrantType())) {
            //验证code值
            if (!AuthUtil.isExistCode(tokenRequestDTO.getCode())) {
                return "code is error";
            }

            //授权码一旦被使用，须要立即作废
            AuthUtil.codeMap.remove(tokenRequestDTO.getCode());

            //生成访问令牌access_token的值  user在生成code时和code进项了绑定  所以这里可以拿到
            String accessToken = AuthUtil.generateAccessToken(tokenRequestDTO.getAppId(), "USERTEST");

            //授权范围与访问令牌绑定
            tokenScopeMap.put(accessToken, codeScopeMap.get(tokenRequestDTO.getCode()));

            //生成刷新令牌refresh_token的值
            String refreshToken = AuthUtil.generateRefreshToken(tokenRequestDTO.getCode(), "USERTEST");

            //将accessToken和refreshToken做绑定
            refreshAccessMap.put(refreshToken, accessToken);

            //将refreshToken和codeScopeMap做绑定
            tokenScopeMap.put(refreshToken, codeScopeMap.get(tokenRequestDTO.getCode()));
            return accessToken + "|" + refreshToken;

        } else if (GrantTypeConstant.REFRESH_TOKEN.equals(tokenRequestDTO.getGrantType())) {
            //处理刷新令牌请求环节
            String refreshToken = tokenRequestDTO.getRefreshToken();
            if (!AuthUtil.refreshTokenMap.containsKey(refreshToken)) {
                //该refresh_token值不存在
                return "refreshToken is not exist";
            }

            String appStr = AuthUtil.refreshTokenMap.get("refresh_token");
            if (!appStr.startsWith(tokenRequestDTO.getAppId() + "|" + "USERTEST")) {
                //该refresh_token值 不是颁发给该 第三方软件的
                return "No permission";
            }

            //生成访问令牌access_token的值
            String accessToken = AuthUtil.generateAccessToken(tokenRequestDTO.getAppId(), "USERTEST");
            // 删除旧的access_token 、删除旧的refresh_token、
            AuthUtil.tokenMap.remove(refreshAccessMap.get(refreshToken));
            AuthUtil.refreshTokenMap.remove(refreshToken);
            tokenScopeMap.remove(refreshAccessMap.get(refreshToken));
            tokenScopeMap.remove(refreshToken);
            //生成新的refresh_token
            String newRefreshToken = AuthUtil.generateRefreshToken(tokenRequestDTO.getCode(), "USERTEST");

            //将accessToken和refreshToken做绑定
            refreshAccessMap.put(newRefreshToken, accessToken);

            //授权范围与访问令牌绑定
            tokenScopeMap.put(accessToken, codeScopeMap.get(tokenRequestDTO.getCode()));
            //将refreshToken和codeScopeMap做绑定
            tokenScopeMap.put(refreshToken, codeScopeMap.get(tokenRequestDTO.getCode()));
            return accessToken;
        }
        return null;
    }
}
