package com.phantom.auth.stract;

import com.phantom.auth.entity.User;
import com.phantom.auth.entity.dto.UserDto;
import com.phantom.auth.entity.vo.UserVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * 用户对象转化类
 *
 * @author lei.tan
 * @version 1.0
 * @date 2024-01-27 21:09
 */
@Mapper
public interface UserMapStruct {

    /**
     * 获取该类自动生成的实现类的实例
     */
    UserMapStruct INSTANCE = Mappers.getMapper(UserMapStruct.class);


    @Mappings({
            @Mapping(source = "id", target = "id")
    })
    UserVo toUserVo(User user);


    User toUser(UserDto userDto);


}
