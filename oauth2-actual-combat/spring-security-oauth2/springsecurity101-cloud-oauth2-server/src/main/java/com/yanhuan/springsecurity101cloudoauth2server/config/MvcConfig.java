package com.yanhuan.springsecurity101cloudoauth2server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置登录页面的视图信息
 *
 * @author YanHuan
 * @date 2020-11-07 16:12
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("login")
                .setViewName("login");
    }
}
