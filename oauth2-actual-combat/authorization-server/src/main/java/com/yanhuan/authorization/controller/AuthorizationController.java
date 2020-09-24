package com.yanhuan.authorization.controller;

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

    static Map<String, String[]> codeScopeMap = new HashMap<>();


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
}
