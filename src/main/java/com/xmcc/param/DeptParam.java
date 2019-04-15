package com.xmcc.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author 张兴林
 * @date 2019-03-21 17:24
 */
@Getter
@Setter
public class DeptParam {

    private Integer id;

    @NotBlank
    @Length(max = 15,min = 2,message = "名字长度需要在2-15之间")
    private String name;

    private Integer parentId = 0;

    @NotNull
    private Integer seq = 0;

    @Length(max = 150,message = "备注长度需要在150字以内")
    private String remark;
}
