package com.yanhuan.springsecurity101cloudoauth2client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * demo
 *
 * @author YanHuan
 * @date 2020-11-14 19:55
 */
@RestController
public class DemoController {

    @Autowired
    OAuth2RestTemplate restTemplate;

    /**
     * 演示登录后才能访问的安全页面
     *
     * @param authentication oauth2
     * @return 页面
     */
    @GetMapping("/securedPage")
    public ModelAndView securedPage(OAuth2Authentication authentication) {
        return new ModelAndView("securedPage")
                .addObject("authentication", authentication);
    }

    /**
     * 演示通过OAuth2RestTemplate调用受保护资源
     *
     * @return 调用结果
     */
    @GetMapping("/remoteCall")
    public String remoteCall() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8081/user/name", String.class);
        return responseEntity.getBody();
    }
}