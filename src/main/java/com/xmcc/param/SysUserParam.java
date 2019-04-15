package com.xmcc.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author 张兴林
 * @date 2019-03-22 19:19
 */
@Getter
@Setter
public class SysUserParam {

    private Integer id;

    @NotBlank(message = "名字不能为空")
    @Length(max = 20,message = "用户名长度需要在20字以内")
    private String username;

    @NotBlank(message = "电话不能为空")
    @Length(max = 13,message = "电话长度需要在13个字以内")
    private String telephone;

    @NotBlank(message = "邮箱不能为空")
    @Length(max = 50,message = "邮箱长度需要在50个字符以内")
    private String mail;

    @NotNull(message = "必须提供用户所在部门")
    private Integer deptId;

    @NotNull(message = "必须指定用户状态")
    private Integer status;

    @Length(max = 200,message = "备注长度需要在200个字以内")
    private String remark;


}
