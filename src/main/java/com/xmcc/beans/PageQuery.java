package com.xmcc.beans;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张兴林
 * @date 2019-03-22 20:23
 */
public class PageQuery<T> {

    @Getter
    @Setter
    @Min(value = 1,message = "值必须大于等于1")
    private Integer pageNo = 1;

    @Getter
    @Setter
    @Min(value = 1,message = "值必须大于等于1")
    private Integer pageSize = 5;

    @Setter
    private Integer offset = 1;

    @Getter
    @Setter
    private int total = 0;

    @Getter
    @Setter
    List<T> data = new ArrayList<>();

    public Integer getOffset() {
        return (pageNo - 1) * pageSize;
    }
}
