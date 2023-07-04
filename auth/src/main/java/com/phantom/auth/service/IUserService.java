package com.phantom.auth.service;

import com.phantom.auth.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.phantom.auth.util.BaseResultPage;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author lei.tan
 * @since 2023-04-19
 */
public interface IUserService extends IService<User> {

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户
     */
    User getUserByUsername(String username);

    /**
     * 批量查询用户信息
     *
     * @param user 用户查询条件
     * @return 用户集合
     */
    List<User> selectUsers(User user);

    /**
     * 分页查询用户信息
     *
     * @param user     查询条件
     * @param pageNum  当前第几页
     * @param pageSize 每页多少条数据
     * @return 分页数据
     */
    BaseResultPage<User> getUserByPage(User user, int pageNum, int pageSize);


}
