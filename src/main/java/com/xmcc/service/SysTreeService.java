package com.xmcc.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.xmcc.dao.SysAclMapper;
import com.xmcc.dao.SysDeptMapper;
import com.xmcc.dto.SysAclDto;
import com.xmcc.dto.SysAclModuleDto;
import com.xmcc.dto.SysDeptLevelDto;
import com.xmcc.model.SysAcl;
import com.xmcc.model.SysDept;
import com.xmcc.utils.LevelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 张兴林
 * @date 2019-03-21 20:12
 */
@Service
public class SysTreeService {

    @Autowired
    private SysDeptMapper sysDeptMapper;

    /**
     * 1、获取所有部门
     * 2、将SysDept转换成SysDeptLevelDto
     * 3、判断是否拥有子部门
     * 4、将子部门添加到dto里面
     * 5、递归
     * @return
     */
    //生成树
    public List<SysDeptLevelDto> deptTree(){

        //获取当前所有部门
        List<SysDept> deptList = sysDeptMapper.findAllDept();

        //将dept转换成deptDto(继承dept，拥有子部门集合)
        List<SysDeptLevelDto> sysDeptLevelDtoList = new ArrayList<>();
        for (SysDept sysDept : deptList) {
            //调用dto里面的方法，将dept封装成dto对象
            SysDeptLevelDto dto = SysDeptLevelDto.ADAPTER(sysDept);
            sysDeptLevelDtoList.add(dto);
        }
        sysDeptLevelDtoList = deptListToTree(sysDeptLevelDtoList);
        return sysDeptLevelDtoList;
    }

    /**
     * 封装dto
     * @param sysDeptLevelDtoList
     * @return
     */
    public List<SysDeptLevelDto> deptListToTree(List<SysDeptLevelDto> sysDeptLevelDtoList){
        if (sysDeptLevelDtoList == null){
            return new ArrayList<>(sysDeptLevelDtoList);
        }
        //按部门封装数据
        Multimap<String,SysDeptLevelDto> multimap = ArrayListMultimap.create();

        //创建集合存储顶层部门
        List<SysDeptLevelDto> rootList = new ArrayList<>();
        for (SysDeptLevelDto dto :
                sysDeptLevelDtoList) {
            if (LevelUtil.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
            multimap.put(dto.getLevel(),dto);
        }
        //根据seq排序
        Collections.sort(rootList,new Compareble());
        //递归生成树
        DeptTree(rootList,multimap);
        return rootList;
    }


    public void DeptTree(List<SysDeptLevelDto> rootList,Multimap<String,SysDeptLevelDto> multimap){

        //遍历该层的每一个元素
        for (int i = 0; i < rootList.size(); i++) {
            SysDeptLevelDto dto = rootList.get(i);
            String nextLevel = LevelUtil.calculate(dto.getLevel(),dto.getId());
            List<SysDeptLevelDto> sysDeptLevelDtos = (List<SysDeptLevelDto>) multimap.get(nextLevel);
            if (sysDeptLevelDtos != null){
                //根据seq排序
                Collections.sort(sysDeptLevelDtos,new Compareble());
                //设置下一层部门
                dto.setDeptList(sysDeptLevelDtos);
                //递归
                DeptTree(sysDeptLevelDtos,multimap);
            }
        }
    }

    public class Compareble implements Comparator<SysDeptLevelDto> {
        @Override
        public int compare(SysDeptLevelDto o1, SysDeptLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    }


}
