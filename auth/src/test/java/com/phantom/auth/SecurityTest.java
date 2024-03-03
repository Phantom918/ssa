package com.phantom.auth;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 安全测试
 *
 * @author lei.tan
 * @version 1.0
 * @date 2023/7/15 23:56
 */
@Slf4j
@SpringBootTest
public class SecurityTest {


    @Resource
    private PasswordEncoder passwordEncoder;


    @Test
    void passwordEncoderTest() {
        String password = "123456";
        String encode = passwordEncoder.encode(password);
        log.info("encode: {}", encode);
        boolean matches = passwordEncoder.matches(password, encode);
        log.info("matches: {}", matches);
    }


}
