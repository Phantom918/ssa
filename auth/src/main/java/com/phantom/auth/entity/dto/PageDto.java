package com.phantom.auth.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页属性
 *
 * @author lei.tan
 * @version 1.0
 * @date 2024-01-27 20:46
 */
@Data
@Schema(title = "PageDto", description = "分页对象")
public class PageDto implements Serializable {


    @Schema(title = "当前页码")
    private long pageNum;

    @Schema(title = "每页多少条数据")
    private long pageSize;

}
