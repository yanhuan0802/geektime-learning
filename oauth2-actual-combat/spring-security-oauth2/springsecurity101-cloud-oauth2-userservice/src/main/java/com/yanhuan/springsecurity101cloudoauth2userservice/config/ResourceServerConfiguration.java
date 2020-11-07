package com.yanhuan.springsecurity101cloudoauth2userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

/**
 * 资源服务器配置类
 * <p>
 * 注意两点：
 * 1. 这里硬编码了资源服务器的 ID 为 userservice；
 * 2. 现在我们使用的是不落数据库的 JWT 方式 + 非对称加密，需要通过本地公钥进行验证，因此在这里我们配置了公钥的路径。
 *
 * @author YanHuan
 * @date 2020-11-07 17:23
 * @see EnableResourceServer 启用资源服务器
 * @see EnableGlobalMethodSecurity 启用方法注解方式来进行权限控制
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    /**
     * 声明了资源服务器的ID是userservice，声明了资源服务器的TokenStore是JWT
     *
     * @param resources 资源服务器配置
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId("userservice")
                .tokenStore(tokenStore());
    }

    /**
     * 配置TokenStore
     *
     * @return tokenStore
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * 配置公钥
     *
     * @return 秘钥转换
     */
    @Bean
    protected JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        Resource resource = new ClassPathResource("public.cert");
        String publicKey = null;
        try {
            publicKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        converter.setVerifierKey(publicKey);
        return converter;
    }

    /**
     * 配置了除了/user路径之外的请求可以匿名访问
     *
     * @param http http安全配置
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/**")
                .authenticated()
                .anyRequest().permitAll();
    }
}
