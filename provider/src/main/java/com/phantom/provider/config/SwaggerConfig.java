package com.phantom.provider.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        info.title("Provider Restful API");
        info.description("提供者微服务API文档");
        info.license(new License().name("Apache 2.0").url("https://springdoc.org"));
        openAPI.info(info);
        // 外部文档信息
        ExternalDocumentation documentation = new ExternalDocumentation();
        documentation.url("https://springdoc.org/v2");
        documentation.description("SpringDoc Wiki Documentation");
        openAPI.externalDocs(documentation);
        return openAPI;
    }


}
