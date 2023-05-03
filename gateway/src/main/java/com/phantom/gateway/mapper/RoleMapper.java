package com.phantom.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.phantom.gateway.entity.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色 Mapper 接口
 * </p>
 *
 * @author lei.tan
 * @since 2023-04-20
 */
public interface RoleMapper extends BaseMapper<Role> {


    /**
     * 根据用户id查询角色
     *
     * @param userId 用户id
     * @return 角色列表
     */
    List<Role> selectRoleByUserId(@Param("userId") String userId);

}
