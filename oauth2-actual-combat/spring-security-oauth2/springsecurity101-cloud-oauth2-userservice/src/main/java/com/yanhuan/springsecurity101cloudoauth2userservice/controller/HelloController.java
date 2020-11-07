package com.yanhuan.springsecurity101cloudoauth2userservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用来测试无需登录就可以访问的服务端资源
 *
 * @author YanHuan
 * @date 2020-11-07 17:12
 */
@RestController
public class HelloController {


    @GetMapping("hello")
    public String hello() {
        return "Hello";
    }
}
