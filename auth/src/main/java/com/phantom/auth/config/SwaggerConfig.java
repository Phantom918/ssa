package com.phantom.auth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * swagger3 配置
 *
 * @author lei.tan
 * @version 1.0
 * @date 2023/4/18 14:37
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenApi() {
        OpenAPI openAPI = new OpenAPI();
        // 基础信息
        Info info = new Info();
        info.version("V1.0");
        info.title("Auth Restful API");
        info.description("权限微服务API文档");
        info.license(new License().name("Apache 2.0").url("https://springdoc.org"));
        openAPI.info(info);

        // 外部文档信息
        ExternalDocumentation documentation = new ExternalDocumentation();
        documentation.url("https://springdoc.org/v2");
        documentation.description("SpringDoc Wiki Documentation");
        openAPI.externalDocs(documentation);

        // security 设置
        SecurityScheme security = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                //如果是Http类型，Scheme是必填的，这里是Bearer
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION)
                .description("Bearer JWT Token");

        List<SecurityRequirement> securityRequirements = new ArrayList<>();
        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList("authScheme");
        securityRequirements.add(securityRequirement);
        openAPI.security(securityRequirements);

        Components components = new Components();
        // 这里的 key: "authScheme" 在方法注解上使用，则会在请求头带上 Authorization的信息 @Operation(summary = "测试接口test1", security = @SecurityRequirement(name = "authScheme"))
        components.addSecuritySchemes("authScheme", security);
        openAPI.components(components);

        List<Server> servers = new ArrayList<>(); //多服务
        // 表示服务器地址或者URL模板列表，多个服务地址随时切换（只不过是有多台IP有当前的服务API）
        servers.add(new Server().url("http://localhost:8080").description("服务1"));
        servers.add(new Server().url("http://localhost:8081").description("服务2"));
        // openAPI.servers(servers);

        return openAPI;
    }


//    @Bean
    public WebFilter corsFilter() {
        return (ServerWebExchange ctx, WebFilterChain chain) -> {
            ServerHttpRequest request = ctx.getRequest();
            HttpHeaders headers = request.getHeaders();
            if (headers.getOrigin() != null) {
                ServerHttpResponse response = ctx.getResponse();
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, headers.getOrigin());
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "*");
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
                response.getHeaders().add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
                if (request.getMethod() == HttpMethod.OPTIONS) {
                    response.setStatusCode(HttpStatus.OK);
                    return Mono.empty();
                }
            }
            return chain.filter(ctx);
        };
    }


}
