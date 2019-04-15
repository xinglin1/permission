package com.xmcc.dto;

import com.xmcc.model.SysDept;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张兴林
 * @date 2019-03-21 20:07
 */
@Getter
@Setter
public class SysDeptLevelDto extends SysDept {

    //用来存储下层数据
    List<SysDeptLevelDto> deptList = new ArrayList<>();

    //将sysDept封装成sysDeptDto
    public static SysDeptLevelDto ADAPTER(SysDept sysDept){

        SysDeptLevelDto sysDeptLevelDto = new SysDeptLevelDto();

        //拷贝字段
        BeanUtils.copyProperties(sysDept,sysDeptLevelDto);
        return sysDeptLevelDto;
    }

}
