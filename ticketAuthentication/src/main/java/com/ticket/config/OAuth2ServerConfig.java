package com.ticket.config;


import com.ticket.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.sql.DataSource;


/**
 * 前置校验
     * 在请求到达/oauth/token之前经过ClientCredentialsTokenEndpointFilter过滤器 # attemptAuthentication
     * 顶级身份管理者AuthenticationManager（AuthenticationManager的实现类一般是ProviderManager）
     *   -->ProviderManager内部维护了一个List,真正的身份认证是由一系列AuthenticationProvider去完成
     *   -->AuthenticationProvider的常用实现类则是DaoAuthenticationProvider
     *        --->DaoAuthenticationProvider内部又聚合了一个UserDetailsService接口:获取用户详细信息的最终接口
 *Token处理端点TokenEndpoint #postAccessToken
 *  -->OAuth2AccessToken(token序列化之前的原始类)的实现类DefaultOAuth2AccessToken
 *  -->CompositeTokenGranter#grant:循环调用五种TokenGranter实现类的grant方法,通过grantType来区分是否是各自的授权类型
 *               --->5种授权者的抽象类：AbstractTokenGranter (
 *                     --->3个重要属性：AuthorizationServerTokenServices（token相关的service）
 *                                      ClientDetailsService （clientDetails相关的service）
 *                                      OAuth2RequestFactory （创建oauth2Request的工厂）
 *
 * AuthorizationServerTokenServices（创建token 刷新token 获取token）
 *  -->默认的实现类DefaultTokenServices 在创建token时，他会调用tokenStore对产生的token和相关信息存储到对应的实现类中，可以是redis，数据库，内存，jwt
 */

/**
 * 配置授权的相关信息
 *  配置客户端、配置token存储方式等
 */
@Configuration
@EnableAuthorizationServer//开启配置 OAuth 2.0 认证授权服务
public class OAuth2ServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    DataSource dataSource;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * 配置OAuth2的客户端相关信息
         * 配置 oauth_client_details【client_id和client_secret等】信息的认证【检查ClientDetails的合法性】服务
         * 设置 认证信息的来源：数据库 (可选项：数据库和内存,使用内存一般用来作测试)
         * 自动注入：ClientDetailsService的实现类 JdbcClientDetailsService (检查 ClientDetails 对象)
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }

    /**
     *
     * 密码模式下配置认证管理器 AuthenticationManager,并且设置 AccessToken的存储介质tokenStore,如果不设置，则会默认使用内存当做存储介质。
     * 而该AuthenticationManager将会注入 2个Bean对象用以检查(认证)
     * 1、ClientDetailsService的实现类 JdbcClientDetailsService (检查 ClientDetails 对象)
     * 2、UserDetailsService的实现类 CustomUserDetailsService (检查 UserDetails 对象)
     *
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {
        endpoints.authenticationManager(authenticationManager).tokenStore(tokenStore).userDetailsService(userDetailsService);
    }

    /**
     *  配置：安全检查流程
     *  默认过滤器：BasicAuthenticationFilter
     *  1、oauth_client_details表中clientSecret字段加密【ClientDetails属性secret】
     *  2、CheckEndpoint类的接口 oauth/check_token 无需经过过滤器过滤，默认值：denyAll()
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients();//允许客户表单认证
        security.passwordEncoder(new BCryptPasswordEncoder());//设置oauth_client_details中的密码编码器
        security.checkTokenAccess("permitAll()");//对于CheckEndpoint控制器[框架自带的校验]的/oauth/check端点允许所有客户端发送器请求而不会被Spring-security拦截
    }

}
