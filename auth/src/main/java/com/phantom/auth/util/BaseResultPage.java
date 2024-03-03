package com.phantom.auth.util;


import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 基础消息分页对象
 *
 * @author lei.tan
 * @version 1.0
 * @date 2023/6/30 14:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResultPage<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 信息
     */
    private String message;

    /**
     * 数据体
     */
    private List<T> data;


    /**
     * 当前页码
     */
    private long pageNum;

    /**
     * 每页多少条数据
     */
    private long pageSize;

    /**
     * 总共多少页数据
     */
    private long totalPage;

    /**
     * 总共多少条数据
     */
    private long totalCount;

    public static <T> BaseResultPage<T> success(IPage<T> page) {
        BaseResultPage<T> resultPage = new BaseResultPage<>();
        resultPage.setCode(StatusEnum.SUCCESS.getValue());
        resultPage.setMessage(StatusEnum.SUCCESS.getDescription());
        // 数据
        resultPage.setData(page.getRecords());
        // 当前第几页
        resultPage.setPageNum(page.getCurrent());
        // 每页多少条
        resultPage.setPageSize(page.getSize());
        // 总共多少页
        resultPage.setTotalPage(page.getPages());
        // 总共多少条
        resultPage.setTotalCount(page.getTotal());

        return resultPage;
    }

    public static <T> BaseResultPage<T> success(List<T> data, int pageNum, int pageSize, int totalPage, long totalCount) {
        BaseResultPage<T> resultPage = new BaseResultPage<>();
        resultPage.setCode(StatusEnum.SUCCESS.getValue());
        resultPage.setMessage(StatusEnum.SUCCESS.getDescription());
        resultPage.setData(data);
        resultPage.setPageNum(pageNum);
        resultPage.setPageSize(pageSize);
        resultPage.setTotalPage(totalPage);
        resultPage.setTotalCount(totalCount);

        return resultPage;
    }



    public static BaseResultPage<String> error() {
        BaseResultPage<String> resultPage = new BaseResultPage<>();
        resultPage.setCode(StatusEnum.ERROR.getValue());
        resultPage.setMessage(StatusEnum.ERROR.getDescription());

        return resultPage;
    }


}
