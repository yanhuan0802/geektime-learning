package com.yanhuan.springsecurity101cloudoauth2userservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 需要登录 + 授权才能访问到的接口
 *
 * @author YanHuan
 * @date 2020-11-07 17:13
 */
@RestController
@RequestMapping("user")
public class UserController {

    private final TokenStore tokenStore;

    public UserController(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    /***
     * 读权限或写权限可访问，返回登录用户名
     * @param authentication 身份验证令牌
     * @return 登录用户名
     */
    @PreAuthorize("hasAuthority('READ') or hasAuthority('WRITE')")
    @GetMapping("name")
    public String name(OAuth2Authentication authentication) {
        return authentication.getName();
    }

    /**
     * 读权限或写权限可访问，返回登录用户信息
     *
     * @param authentication 身份验证令牌
     * @return 用户信息
     */
    @PreAuthorize("hasAuthority('READ') or hasAuthority('WRITE')")
    @GetMapping
    public OAuth2Authentication read(OAuth2Authentication authentication) {
        return authentication;
    }

    /**
     * 只有写权限可以访问，返回访问令牌中的额外信息
     * （也就是自定义的 Token 增强器 CustomTokenEnhancer 加入到访问令牌中的额外信息，Key 是 userDetails）
     * 这里也演示了使用 TokenStore 来解析 Token 的方式。
     *
     * @param authentication 身份验证令牌
     * @return 返回访问令牌中的额外信息
     */
    @PreAuthorize("hasAuthority('WRITE')")
    @PostMapping
    public Object write(OAuth2Authentication authentication) {
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(details.getTokenValue());
        return accessToken.getAdditionalInformation().getOrDefault("userDetails", null);
    }
}
