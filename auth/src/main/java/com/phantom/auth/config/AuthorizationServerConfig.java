package com.phantom.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.oauth2.server.authorization.web.OAuth2ClientAuthenticationFilter;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;


/**
 * 授权服务器配置
 *
 * @author leitan
 * @date 2023/07/16
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {


    /**
     * 自定义身份验证入口点
     */
    @Resource
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    /**
     * 自定义访问被拒绝处理程序
     */
    @Resource
    private CustomAccessDeniedHandler customAccessDeniedHandler;


    /**
     * 自定义授权界面 url
     */
    private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";

    /**
     * 密码编码器
     */
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 授权服务器安全过滤器链
     *
     * @param http http
     * @return {@link SecurityFilterChain}
     * @throws Exception 异常
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

        // 授权服务器配置
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        // 授权设置
        authorizationServerConfigurer
                .authorizationEndpoint(configurer -> {
                    // 设置授权界面 url
                    configurer.consentPage(CUSTOM_CONSENT_PAGE_URI);
                    // 授权异常处理，可以抽出成为配置类
                    configurer.errorResponseHandler((request, response, exception) -> {
                        OAuth2AuthenticationException authenticationException = (OAuth2AuthenticationException) exception;
                        OAuth2Error error = authenticationException.getError();
                        log.error("错误原因:[{}]", error);
                        log.info("认证异常", exception);
                        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                        response.setContentType(MediaType.APPLICATION_JSON.toString());
                        response.getWriter().write("{\"code\":-1,\"msg\":\"授权认证失败\"}");
                    });
                });

        // token 设置
        authorizationServerConfigurer
                .tokenEndpoint(configurer ->
                        configurer.errorResponseHandler((request, response, exception) -> {
                            OAuth2AuthenticationException oAuth2AuthenticationException = (OAuth2AuthenticationException) exception;
                            OAuth2Error error = oAuth2AuthenticationException.getError();
                            log.error("错误原因:[{}]", error);
                            log.info("认证异常", exception);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.setContentType(MediaType.APPLICATION_JSON.toString());
                            response.getWriter().write("{\"code\":-1,\"msg\":\"token认证失败\"}");
                        }));


        // Enable OpenID Connect 1.0
        authorizationServerConfigurer
                .oidc(Customizer.withDefaults())
                .addObjectPostProcessor(new ObjectPostProcessor<Object>() {
                    @Override
                    public <O> O postProcess(O object) {
                        if (object instanceof OAuth2ClientAuthenticationFilter) {
                            OAuth2ClientAuthenticationFilter filter = (OAuth2ClientAuthenticationFilter) object;
                            filter.setAuthenticationFailureHandler((request, response, exception) -> {
                                OAuth2AuthenticationException oAuth2AuthenticationException = (OAuth2AuthenticationException) exception;
                                OAuth2Error oAuth2Error = oAuth2AuthenticationException.getError();
                                switch (oAuth2Error.getErrorCode()) {
                                    case OAuth2ErrorCodes.INVALID_CLIENT:
                                        log.info("无效的客户端...");
                                        break;
                                    case OAuth2ErrorCodes.ACCESS_DENIED:
                                        log.info("无权限访问...");
                                        break;
                                    case OAuth2ErrorCodes.INVALID_GRANT:
                                        log.info("无效的授权码...");
                                        break;
                                    default:
                                        break;
                                }
                                log.error("错误原因:[{}]", oAuth2Error);
                                log.info("认证异常", exception);
                                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                                response.setContentType(MediaType.APPLICATION_JSON.toString());
                                response.getWriter().write("{\"code\":-2,\"msg\":\"认证失败!\"}");
                            });
                        }
                        return object;
                    }
                });

        // 设置授权服务器的配置
        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

        http
                // 拦截对 授权服务器 相关端点的请求
                .securityMatcher(endpointsMatcher)
                // 拦截到的请求需要登录
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                // 忽略掉相关端点的csrf（跨站请求）：对授权端点的访问可以是跨站的
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                /* .headers(headers ->
                        headers
                                .cacheControl().disable() // 禁用HTTP响应标头
                                .frameOptions().disable()
                ) */
                .exceptionHandling(exceptions ->
                                exceptions
                                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                        // 登录异常处理
                        // .authenticationEntryPoint(customAuthenticationEntryPoint)
                        // 权限校验异常处理
                        // .accessDeniedHandler(customAccessDeniedHandler)
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .apply(authorizationServerConfigurer);

        return http.build();
    }

    /**
     * 注册客户端
     *
     * @param jdbcTemplate 数据库操作对象
     * @return
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        String providerClient = "provider";
        String microService = "micro_service";
        // 注册客户端保存到数据库
        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);

        if (null == registeredClientRepository.findByClientId(providerClient)) {
            RegisteredClient registeredClient = createRegisteredClientForClient(providerClient, providerClient, "123456");
            registeredClientRepository.save(registeredClient);
        }
        if (null == registeredClientRepository.findByClientId(microService)) {
            RegisteredClient registeredClient = createRegisteredClientForMicroService(microService, microService, "123456");
            registeredClientRepository.save(registeredClient);
        }

        return registeredClientRepository;
    }


    /**
     * 注册客户端，(主要用于: 客户端来注册)
     *
     * @param clientId   客户端唯一id
     * @param clientName 客户端名称
     * @param password   客户端密码
     * @return RegisteredClient 注册客户端对象
     */
    public RegisteredClient createRegisteredClientForClient(String clientId, String clientName, String password) {
        // JWT(Json Web Token)配置
        TokenSettings tokenSettings = TokenSettings.builder()
                // 令牌存活时间：2小时
                .accessTokenTimeToLive(Duration.ofHours(2))
                // 令牌可以刷新，重新获取
                .reuseRefreshTokens(true)
                // 刷新时间：10天（10天内当令牌过期时，可以用刷新令牌重新申请新令牌，不需要再认证）
                .refreshTokenTimeToLive(Duration.ofDays(10))
                .build();

        // 客户端配置
        ClientSettings clientSettings = ClientSettings.builder()
                // 是否需要用户授权确认的界面这一过程
                .requireAuthorizationConsent(true)
                .build();

        return RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId(clientId)
                //.clientSecret("{noop}123456")
                .clientSecret(passwordEncoder.encode(password))
                // 客户端名称(可省略)
                .clientName(clientName)
                // 授权方法
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                // 授权类型: 授权码模式
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                // 授权类型: 刷新令牌(授权码模式)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                // JWT_BEARER
                .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
                // 回调地址: 授权服务器向当前客户端响应后，会回调下面地址
                // 不在此列的地址将被拒统，建议使用IP或域名，不要使用localhost
                .redirectUri("http://127.0.0.1:8000/login/oauth2/code/myClient")
                .redirectUri("http://127.0.0.1:3000/token")
                .redirectUri("https://www.baidu.com")
                // 授权范围(此客户端能够授权的范围，名称自定义)
                .scope("user.userInfo")
                .scope("ROLE_ADMIN")
                // JWT配置
                .tokenSettings(tokenSettings)
                // 客户端配置
                .clientSettings(clientSettings)
                .build();
    }

    /**
     * 注册客户端(主要用于: 内部微服务之间的调用者来注册)
     *
     * @param clientId   客户端唯一id
     * @param clientName 客户端名称
     * @param password   客户端密码
     * @return RegisteredClient 注册客户端对象
     */
    public RegisteredClient createRegisteredClientForMicroService(String clientId, String clientName, String password) {
        // JWT(Json Web Token)配置
        TokenSettings tokenSettings = TokenSettings.builder()
                // 令牌存活时间：2小时
                .accessTokenTimeToLive(Duration.ofHours(2))
                // 令牌不允许刷新
                .reuseRefreshTokens(false)
                .build();

        // 客户端配置
        ClientSettings clientSettings = ClientSettings.builder()
                // 是否需要用户授权确认的界面这一过程
                .requireAuthorizationConsent(false).build();

        return RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId(clientId)
                //.clientSecret("{noop}123456")
                .clientSecret(passwordEncoder.encode(password))
                // 客户端名称(可省略)
                .clientName(clientName)
                // 授权方法
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                // 授权类型: 客户端模式
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                // 授权类型: 刷新令牌(授权码模式)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                // JWT_BEARER
                .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
                // 回调地址: 授权服务器向当前客户端响应后，会回调下面地址
                // 不在此列的地址将被拒统，建议使用IP或域名，不要使用localhost
                .redirectUri("http://127.0.0.1:8001")
                .redirectUri("http://127.0.0.1:3000/token")
                .redirectUri("https://www.baidu.com")
                // 授权范围(此客户端能够授权的范围，名称自定义)
                .scope("all")
                // JWT配置
                .tokenSettings(tokenSettings)
                // 客户端配置
                .clientSettings(clientSettings)
                .build();
    }


    /**
     * 把资源拥有者授权确认操作保存到数据库
     * 资源拥有者（Resource Owner）对客户端的授权记录
     *
     * @param jdbcTemplate               操作数据库
     * @param registeredClientRepository 客户端仓库
     * @return
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    /**
     * 令牌的发放记录
     * 把token下发信息保存到数据库（自己看需要是否需要保存这种记录再去决定是否配置）
     * 资源拥有者（Resource Owner）对客户端的授权记录
     *
     * @param jdbcTemplate               操作数据库
     * @param registeredClientRepository 客户端仓库
     * @return 授权服务
     */
//    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }


    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        // 官方配置
//        RSAKey rsaKey = Jwks.generateRsa();
//        JWKSet jwkSet = new JWKSet(rsaKey);
//        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);

        // gateway-oauth2 自定义配置
        // 加载证书 读取类路径文件
        org.springframework.core.io.Resource resource = new FileSystemResource("/Users/leitan/WorkSpace/IdeaSpace/ssa/new-authoriza-server.jks");
        // 创建秘钥工厂(加载读取证书数据)
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, "123456" .toCharArray());
        // 读取秘钥对(公钥、私钥)
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair("new-authoriza-server", "123456" .toCharArray());
        // 读取公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        // 读取私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return jwt -> {
            log.info("自定义添加token信息.....");
            JwsHeader.Builder headers = jwt.getJwsHeader();
            JwtClaimsSet.Builder claims = jwt.getClaims();
            // access_token 自定义
            if (jwt.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
                log.info("access_token 自定义....");
                // 自定义 header
                headers.header("customerHeader", "这是一个自定义header");
                // 自定义 claim(也就是对应第二段 PAYLOAD)
                claims.claim("customerClaim", "这是一个自定义Claim");
            } else if (jwt.getTokenType().getValue().equals(OidcParameterNames.ID_TOKEN)) {
                // id_token 自定义
            }
        };
    }

    @Bean
    public OAuth2TokenGenerator<?> tokenGenerator(JwtEncoder jwtEncoder, OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer) {
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        jwtGenerator.setJwtCustomizer(tokenCustomizer);
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
    }


    /**
     * 授权服务信息配置
     * <p>
     * 授权服务器本身也提供了一个配置工具来配置其元信息，大多数都使用默认配置即可，需要配置的
     * 只有授权服务器的地址issuer，在生产中这个地方应该配置为域名
     * </p>
     *
     * @return
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                // 自定义授权页面提交的url
                .authorizationEndpoint("/my/authorize")
                // 发布者的url地址, 一般是授权服务端的路径
                .issuer("http://127.0.0.1:9001").build();
    }


}
