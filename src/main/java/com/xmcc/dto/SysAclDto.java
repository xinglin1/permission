package com.xmcc.dto;

import com.xmcc.model.SysAcl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

/**
 * @author 张兴林
 * @date 2019-03-27 16:12
 */
@Getter
@Setter
public class SysAclDto extends SysAcl {

    private boolean checked = false;

    private boolean hasAcl = false;

    public static SysAclDto ADAPTER(SysAcl sysAcl){

        SysAclDto sysAclDto = new SysAclDto();
        //拷贝字段
        BeanUtils.copyProperties(sysAcl,sysAclDto);
        return sysAclDto;
    }
}
