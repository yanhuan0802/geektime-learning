package com.yanhuan.springsecurity101cloudoauth2server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.util.Arrays;


/**
 * 核心：配置授权服务器
 *
 * @author YanHuan
 * @date 2020-11-07 15:30
 * @see EnableAuthorizationServer 开启授权服务
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2ServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final DataSource dataSource;
    private final AuthenticationManager authenticationManager;

    public OAuth2ServerConfiguration(DataSource dataSource, AuthenticationManager authenticationManager) {
        this.dataSource = dataSource;
        this.authenticationManager = authenticationManager;
    }

    /**
     * 我们配置了使用数据库来维护客户端信息。
     * 虽然在各种Demo中我们经常看到的是在内存中维护客户端信息，通过配置直接写死在这里。
     * 但是，对于实际的应用我们一般都会用数据库来维护这个信息，
     * 甚至还会建立一套工作流来允许客户端自己申请ClientID，实现OAuth客户端接入的审批。
     *
     * @param clients 客户端配置
     * @throws Exception 异常抛出
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }

    /**
     * 首先，打开了验证Token的访问权限（以便之后我们演示）。
     * 然后，允许ClientSecret明文方式保存，并且可以通过表单提交（而不仅仅是Basic Auth方式提交），之后会演示到这个。
     *
     * @param security 服务端配置
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients()
                .passwordEncoder(NoOpPasswordEncoder.getInstance());
    }

    /**
     * 干了以下4件事儿：
     * 1. 配置我们的令牌存放方式为JWT方式，而不是内存、数据库或Redis方式。
     * JWT是Json Web Token的缩写，也就是使用JSON数据格式包装的令牌，由.号把整个JWT分隔为头、数据体、签名三部分。
     * JWT保存Token虽然易于使用但是不是那么安全，一般用于内部，且需要走HTTPS并配置比较短的失效时间。
     * 2. 配置JWT Token的非对称加密来进行签名
     * 3. 配置一个自定义的Token增强器，把更多信息放入Token中
     * 4. 配置使用JDBC数据库方式来保存用户的授权批准记录
     *
     * <p>
     * AuthorizationServerEndpointsConfigurer其实是一个装载类，
     * 装载Endpoints所有相关的类配置（AuthorizationServer、TokenServices、TokenStore、ClientDetailsService、UserDetailsService）。
     *
     * @param endpoints 端点配置
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        //令牌增强器链
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(
                Arrays.asList(tokenEnhancer(), jwtTokenEnhancer()));

        endpoints.approvalStore(approvalStore())
                .authorizationCodeServices(authorizationCodeServices())
                .tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain)
                .authenticationManager(authenticationManager);
    }

    /**
     * 使用JDBC数据库方式来保存授权码
     *
     * @return 存储授权码的服务
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    /**
     * 使用JWT存储
     *
     * @return token存储方式
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtTokenEnhancer());
    }

    /**
     * 使用JDBC数据库方式来保存用户的授权批准记录
     *
     * @return JdbcApprovalStore
     */
    @Bean
    public JdbcApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }

    /**
     * 自定义的Token增强器，把更多信息放入Token中
     *
     * @return token增强器
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }

    /**
     * 配置JWT使用非对称加密方式来验证
     *
     * @return 令牌和人证信息之间的转换方式
     */
    @Bean
    protected JwtAccessTokenConverter jwtTokenEnhancer() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "mySecretKey".toCharArray());
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("jwt"));
        return converter;
    }

}
