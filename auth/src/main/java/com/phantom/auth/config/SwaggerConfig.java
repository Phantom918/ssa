package com.phantom.auth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

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
                .bearerFormat("JWT")
                .scheme("bearer")
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);
        Components components = new Components();
        // 这里的 key: "authScheme" 在方法注解上使用，则会在请求头带上 Authorization的信息 @Operation(summary = "测试接口test1", security = @SecurityRequirement(name = "authScheme"))
        components.addSecuritySchemes("authScheme", security);
        openAPI.components(components);

        return openAPI;
    }


}
