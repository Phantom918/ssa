package com.phantom.gateway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.phantom.gateway.entity.Authority;
import com.phantom.gateway.mapper.AuthorityMapper;
import com.phantom.gateway.service.IAuthorityService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author lei.tan
 * @since 2023-04-20
 */
@Service
public class AuthorityServiceImpl extends ServiceImpl<AuthorityMapper, Authority> implements IAuthorityService {

}
