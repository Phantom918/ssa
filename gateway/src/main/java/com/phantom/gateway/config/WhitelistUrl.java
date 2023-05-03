package com.phantom.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 白名单 url
 *
 * @author lei.tan
 * @version 1.0
 * @date 2023/2/9 22:46
 */
@Data
@Component
@ConfigurationProperties(prefix = "whitelist")
public class WhitelistUrl {

    /**
     * 配置文件读取白名单(直接new出来，防止配置文件未配置时其他地方引用报空指针)
     */
    private List<String> urls = new ArrayList<>();

}
