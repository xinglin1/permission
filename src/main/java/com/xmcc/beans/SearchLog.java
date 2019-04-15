package com.xmcc.beans;

import lombok.*;

import java.util.Date;

/**
 * @author 张兴林
 * @date 2019-04-02 09:28
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchLog {

    private Integer type;

    private String beforeSeg;

    private String afterSeg;

    private String operator;

    private String fromTime;

    private String toTime;

}
