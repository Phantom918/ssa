package com.phantom.gateway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.phantom.gateway.entity.Role;
import com.phantom.gateway.mapper.RoleMapper;
import com.phantom.gateway.service.IRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author lei.tan
 * @since 2023-04-20
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
