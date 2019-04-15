package com.xmcc.param;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author 张兴林
 * @date 2019-03-22 11:11
 */
@Setter
@Getter
public class UserParam {

    @NotBlank(message = "用户名不能为空")
    @Length(max = 20,min = 1,message = "用户名长度在20以内")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(max = 13,min = 1,message = "用户名长度在13以内")
    private String password;
}
