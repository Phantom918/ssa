package com.phantom.auth.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.phantom.auth.entity.User;
import com.phantom.auth.entity.dto.UserDto;
import com.phantom.auth.service.IUserService;
import com.phantom.auth.util.BaseResult;
import com.phantom.auth.util.BaseResultPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * TODO
 *
 * @author lei.tan
 * @version 1.0
 * @date 2023/7/3 15:34
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin //如果未来有网关聚合文档的需求，controller上需要增加@CrossOrigin注解，解决跨域问题。
@Tag(name = "AuthController", description = "权限管理")
public class AuthController {

    @Resource
    private IUserService userService;

    @GetMapping("/getUserByPage")
    @Operation(summary = "用户信息-分页查询", description = "这是用户分页查询的接口，采用的 mybatis-plus 分页", security = @SecurityRequirement(name = "authScheme"))
    @Parameters(value = {
            @Parameter(name = "user", description = "用户查询条件对象", required = true)
    })
    public BaseResultPage<User> getUserByPage(UserDto user) {
        log.info("AuthController => getUserByPage(): user: {}", JSON.toJSONString(user));
        BaseResultPage<User> resultPage = userService.getUserByPage(user);
        log.info("getUserByPage() => 查询结果: {}", resultPage);
        // service 层分页案例，mapper也可以使用这种形式
        /*IPage<User> page = new Page<>(1, 10);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(user.getUsername())) {
            queryWrapper.like("username", user.getUsername());
        }
        if (StrUtil.isNotEmpty(user.getNickname())) {
            queryWrapper.like("nickname", user.getNickname());
        }
        if (StrUtil.isNotEmpty(user.getPhone())) {
            queryWrapper.like("phone", user.getPhone());
        }
        if (user.getStatus() != null) {
            queryWrapper.eq("username", user.getStatus());
        }
        IPage<User> result = userService.page(page, queryWrapper);
        log.info("result: {}", JSON.toJSONString(result));*/

        return resultPage;
    }

}
