package com.phantom.auth.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.phantom.auth.entity.User;
import com.phantom.auth.mapper.UserMapper;
import com.phantom.auth.service.IUserService;
import com.phantom.auth.util.BaseResultPage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author lei.tan
 * @since 2023-04-19
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User getUserByUsername(String username) {
        log.info("UserServiceImpl => getUserByUsername() 入参: {}", username);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public List<User> selectUsers(User user) {
        log.info("UserServiceImpl => selectUsers() 入参: {}", JSON.toJSONString(user));
        return userMapper.selectUsers(user);
    }

    @Override
    public BaseResultPage<User> getUserByPage(User user, int pageNum, int pageSize) {
        log.info("UserServiceImpl => getUserByPage(): user: {},  pageNum: {}, pageSize: {}", JSON.toJSONString(user), pageNum, pageSize);
        pageNum = pageNum <= 0 ? 1 : pageNum;
        pageSize = pageSize <= 0 ? 10 : pageSize;
        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> users = userMapper.selectUsers(page, user);
        return BaseResultPage.success(users);
    }


}
