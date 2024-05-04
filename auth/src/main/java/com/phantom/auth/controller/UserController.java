package com.phantom.auth.controller;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.phantom.auth.entity.User;
import com.phantom.auth.entity.dto.UserDto;
import com.phantom.auth.service.IUserService;
import com.phantom.auth.stract.UserMapStruct;
import com.phantom.auth.util.BaseResult;
import com.phantom.auth.util.BaseResultPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Timer;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author lei.tan
 * @since 2023-04-19
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/user")
@Tag(name = "UserController", description = "用户管理")
public class UserController {

    @Resource
    private IUserService userService;

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


    @PostMapping("/queryByPage")
    @Operation(summary = "用户分页查询")
    @Parameter(name = "userDto", description = "用户对象", required = true, schema = @Schema(implementation = UserDto.class))
    public BaseResultPage<User> queryByPage(@RequestBody UserDto userDto) {
        log.info("AuthController => getUserByPage(): userDto: {}", JSON.toJSONString(userDto));
        log.info("你是我心中的唯一=============1");
        return userService.getUserByPage(userDto);
    }


    @PostMapping("/saveUser")
    @Operation(summary = "保存用户")
    @Parameter(name = "userDto", description = "用户对象", required = true, schema = @Schema(implementation = UserDto.class))
    public BaseResult<String> saveUser(@RequestBody UserDto userDto) {
        log.info("saveUser() => userDto: {}", userDto);
        User user = UserMapStruct.INSTANCE.toUser(userDto);

        boolean result = userService.save(user);
        if (result) {
            return BaseResult.success();
        } else {
            return BaseResult.error("用户保存失败！");
        }
    }

    @PostMapping("/deleteUser/{userId}")
    @Operation(summary = "删除用户")
    @Parameter(name = "userId", description = "用户id", required = true)
    public BaseResult<String> deleteUser(@PathVariable String userId) {
        log.info("deleteUser() => userId: {}", userId);
        boolean result = userService.removeById(userId);
        if (result) {
            return BaseResult.success();
        } else {
            return BaseResult.error("删除用户失败！");
        }
    }

    @PostMapping
    @Operation(summary = "更新用户信息")
    @Parameter(name = "userDto", description = "用户对象", required = true, schema = @Schema(implementation = UserDto.class))
    public BaseResult<String> updateUser(@RequestBody UserDto userDto) {
        log.info("updateUser() => userDto: {}", userDto);
        User user = UserMapStruct.INSTANCE.toUser(userDto);
        boolean result = userService.updateById(user);
        if (result) {
            return BaseResult.success();
        } else {
            return BaseResult.error("更新用户信息失败！");
        }
    }


}
