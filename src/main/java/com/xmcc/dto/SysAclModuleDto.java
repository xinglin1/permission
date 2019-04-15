package com.xmcc.dto;

import com.xmcc.model.SysAclModule;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张兴林
 * @date 2019-03-26 10:27
 */

@Getter
@Setter
public class SysAclModuleDto extends SysAclModule {

    //用来存储下层数据
    List<SysAclModuleDto> aclModuleList = new ArrayList<>();

    List<SysAclDto> aclList = new ArrayList<>();

    //将SysAclModule封装成SysAclModuleDto
    public static SysAclModuleDto ADAPTER(SysAclModule sysAclModule){

        SysAclModuleDto sysAclModuleDto = new SysAclModuleDto();

        //拷贝字段
        BeanUtils.copyProperties(sysAclModule,sysAclModuleDto);
        return sysAclModuleDto;
    }
}
