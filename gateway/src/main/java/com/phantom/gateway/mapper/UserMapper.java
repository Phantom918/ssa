package com.phantom.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phantom.gateway.entity.User;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author lei.tan
 * @since 2023-04-19
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户及其权限信息
     *
     * @param username 用户名
     * @return 用户
     */
    User selectUserAuthorityByUsername(@Param("username") String username);


    /**
     * 根据用户名和url查询权限数量
     *
     * @param username 用户名
     * @param url      url
     * @return 权限数量
     */
    Integer checkAuthorityCount(@Param("username") String username, @Param("url") String url);

}
