package com.phantom.auth.controller;


import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author lei.tan
 * @since 2023-04-19
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/test1")
    public JSONObject test1(@RequestParam("name") String name, @RequestParam("age") Integer age) {
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
