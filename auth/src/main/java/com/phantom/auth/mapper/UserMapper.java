package com.phantom.auth.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.phantom.auth.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    // 如果只有一个入参可以省略 @Param 注解，xml中可以直接使用传入对象的 属性名，不需要 [别名].[属性名]
    // List<User> selectUsers(User user);

    /**
     * 用户信息-查询
     *
     * @param user 用户对象查询条件
     * @return 用户信息集合
     */
    List<User> selectUsers(@Param("user") User user);

    /**
     * 用户信息-分页查询
     *
     * @param page 分页参数
     * @param user 用户对象查询条件
     * @return 分页用户信息
     */
    Page<User> selectUsers(IPage<User> page, @Param("user") User user);

}
