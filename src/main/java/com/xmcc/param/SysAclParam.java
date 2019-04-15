package com.xmcc.param;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author 张兴林
 * @date 2019-03-26 17:30
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SysAclParam {

    private Integer id;

    @NotBlank(message = "名字不能为空")
    @Length(max = 20,min = 2,message = "长度需要在2至20字以内")
    private String name;

    @NotNull(message = "所属模块不能为空")
    private Integer aclModuleId;

    @NotNull(message = "seq不能为空")
    private Integer seq;

    @Length(max = 150,message = "长度需要在150字以内")
    private String remark;

    private Integer status;

    @Length(max = 100,min = 6,message = "长度需要在6-100字以内")
    private String url;

    private Integer type;
}
