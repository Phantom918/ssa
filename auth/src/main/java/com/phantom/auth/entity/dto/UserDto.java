package com.phantom.auth.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;



/**
 * 用户Dto
 *
 * @author lei.tan
 * @version 1.0
 * @date 2024-01-27 20:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "UserDto", description = "用户查询对象")
public class UserDto extends PageDto implements Serializable {

    @Schema(title = "主键")
    private Long id;

    @Schema(title = "用户名")
    private String username;

    @Schema(title = "密码")
    private String password;

    @Schema(title = "别名")
    private String nickname;

    @Schema(title = "性别(0:女 1:男)")
    private Integer sex;

    @Schema(title = "邮箱")
    private String email;

    @Schema(title = "电话")
    private String phone;

    @Schema(title = "状态(0:启用 1:禁用)", defaultValue = "0", allowableValues = {"0", "1"})
    private Integer status;


}
