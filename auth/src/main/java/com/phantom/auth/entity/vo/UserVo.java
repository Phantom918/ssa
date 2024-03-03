package com.phantom.auth.entity.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO
 *
 * @author lei.tan
 * @version 1.0
 * @date 2024-01-27 21:06
 */
@Data
public class UserVo {

    @Schema(title = "主键")
    private Long id;

    @Schema(title = "用户名title", description = "用户名description")
    private String username;

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

    @Schema(title = "创建时间")
    private LocalDateTime createTime;

    @Schema(title = "更新时间")
    private LocalDateTime updateTime;

    @Schema(title = "上次登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(title = "备注")
    private String remark;



}
