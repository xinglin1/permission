package com.xmcc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @author 张兴林
 * @date 2019-04-02 15:13
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchLogDto {
    private Integer type;

    private String beforeSeg;

    private String afterSeg;

    private String operator;

    private Date fromTime;

    private Date toTime;

    public SearchLogDto(Integer type, String beforeSeg, String afterSeg, String operator) {
        this.type = type;
        this.beforeSeg = beforeSeg;
        this.afterSeg = afterSeg;
        this.operator = operator;
    }
}
