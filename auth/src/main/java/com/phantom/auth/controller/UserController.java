package com.phantom.auth.controller;


import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author lei.tan
 * @since 2023-04-19
 */
@CrossOrigin
@RestController
@RequestMapping("/user")
@Tag(name = "UserController", description = "用户管理")
public class UserController {

    @GetMapping("/test1")
    @Operation(summary = "测试接口test1", security = @SecurityRequirement(name = "authScheme"))
    @Parameters(value = {
            @Parameter(name = "name", description = "姓名", required = true),
            @Parameter(name = "age", description = "年龄", required = true)
    })
    public JSONObject test1(String name, Integer age) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("age", age);
        return jsonObject;
    }

    /**
     * 测试接口
     *
     * @return
     */
    @GetMapping("/test2")
    public String test2() {
        return "test2-----";
    }

}
