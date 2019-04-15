package com.xmcc.param;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author 张兴林
 * @date 2019-03-26 20:12
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SysRoleParam {

    private Integer id;

    @NotBlank(message = "名字不能为空")
    @Length(max = 20,min = 2,message = "名字长度需要在2-20以内")
    private String name;

    @Length(max = 150,message = "长度需要在150字以内")
    private String remark;

    private Integer status;

    private Integer type = 1;
}
