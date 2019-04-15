package com.xmcc.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author 张兴林
 * @date 2019-03-26 09:45
 */
@Getter
@Setter
public class SysAclModuleParam {

    private Integer id;

    @NotBlank(message = "name不能为空")
    @Length(max = 15,min = 2,message = "长度需要在2-15字之间")
    private String name;

    private Integer parentId = 0;

    @NotNull(message = "seq不能为空")
    private Integer seq = 0;

    @Length(max = 150,message = "长度需要在150字以内")
    private String remark;

    private Integer status;
}
