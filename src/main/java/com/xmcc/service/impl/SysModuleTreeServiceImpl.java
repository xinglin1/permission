package com.xmcc.service.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.xmcc.dao.SysAclMapper;
import com.xmcc.dao.SysAclModuleMapper;
import com.xmcc.dao.SysDeptMapper;
import com.xmcc.dao.SysRoleAclMapper;
import com.xmcc.dto.SysAclDto;
import com.xmcc.dto.SysAclModuleDto;
import com.xmcc.dto.SysDeptLevelDto;
import com.xmcc.model.SysAcl;
import com.xmcc.model.SysAclModule;
import com.xmcc.model.SysDept;
import com.xmcc.model.SysRoleAcl;
import com.xmcc.param.SysAclModuleParam;
import com.xmcc.service.SysAclModuleService;
import com.xmcc.service.SysCoreService;
import com.xmcc.service.SysModuleTreeService;
import com.xmcc.utils.LevelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author 张兴林
 * @date 2019-03-21 20:12
 */
@Service
public class SysModuleTreeServiceImpl implements SysModuleTreeService {

    @Autowired
    private SysAclModuleMapper sysAclModuleMapper;

    @Autowired
    private SysRoleAclMapper sysRoleAclMapper;

    @Autowired
    private SysCoreService sysCoreService;

    @Autowired
    private SysAclMapper sysAclMapper;

    /**
     * 1、获取所有部门
     * 2、将SysDept转换成SysDeptLevelDto
     * 3、判断是否拥有子部门
     * 4、将子部门添加到dto里面
     * 5、递归
     * @return
     */
    //生成树
    @Override
    public List<SysAclModuleDto> deptTree(){

        //获取当前所有角色
        List<SysAclModule> moduleList = sysAclModuleMapper.findAllModule();

        //将dept转换成deptDto(继承dept，拥有子部门集合)
        List<SysAclModuleDto> sysAclModuleDtoList = new ArrayList<>();
        for (SysAclModule module : moduleList) {
            //调用dto里面的方法，将dept封装成dto对象
            SysAclModuleDto dto = SysAclModuleDto.ADAPTER(module);
            sysAclModuleDtoList.add(dto);
        }
        sysAclModuleDtoList = deptListToTree(sysAclModuleDtoList);
        return sysAclModuleDtoList;
    }

    /**
     * 封装dto
     * @param sysAclModuleDtoList
     * @return
     */
    @Override
    public List<SysAclModuleDto> deptListToTree(List<SysAclModuleDto> sysAclModuleDtoList){
        if (sysAclModuleDtoList == null){
            return new ArrayList<>(sysAclModuleDtoList);
        }
        Multimap<String,SysAclModuleDto> multimap = ArrayListMultimap.create();

        List<SysAclModuleDto> rootList = new ArrayList<>();
        for (SysAclModuleDto dto :
                sysAclModuleDtoList) {
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


    @Override
    public void DeptTree(List<SysAclModuleDto> rootList,Multimap<String,SysAclModuleDto> multimap){

        //遍历该层的每一个元素
        for (int i = 0; i < rootList.size(); i++) {
            SysAclModuleDto dto = rootList.get(i);
            String nextLevel = LevelUtil.calculate(dto.getLevel(),dto.getId());
            List<SysAclModuleDto> sysAclModuleDtos = (List<SysAclModuleDto>) multimap.get(nextLevel);
            if (sysAclModuleDtos != null){
                //根据seq排序
                Collections.sort(sysAclModuleDtos,new Compareble());
                //设置下一层权限
                dto.setAclModuleList(sysAclModuleDtos);
                //递归
                DeptTree(sysAclModuleDtos,multimap);
            }
        }
    }

    public class Compareble implements Comparator<SysAclModuleDto> {
        @Override
        public int compare(SysAclModuleDto o1, SysAclModuleDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    }

    /**
     * 1、通过登录用户user_id在sys_role_user表查询到role_id
     * 2、通过role_id在sys_role_acl表查询acl_id集合
     * 3、通过acl_id集合在sys_acl表查询acl集合
     */
    /**
     * 生成角色权限树
     */
    @Override
    public List<SysAclModuleDto> roleTree(Integer roleId){
        //获取当前用户拥有的权限
        List<SysAcl> userAclList = sysCoreService.getUserAclList();
        if (userAclList == null){
            return new ArrayList<>();
        }
        //获取角色对应权限
        List<SysAcl> roleAclList = sysCoreService.getRoleAclList(roleId);
        if (roleAclList == null){
            return new ArrayList<>();
        }
        //获取所有
        List<SysAcl> all = sysAclMapper.getAll();
        //存储AclDto
        List<SysAclDto> aclDtoList = new ArrayList<>();
        for (SysAcl sysAcl : all) {
            SysAclDto sysAclDto = SysAclDto.ADAPTER(sysAcl);
            //如果当前用户有操作权限
            if (userAclList.contains(sysAcl)){
                sysAclDto.setHasAcl(true);
            }
            //如果角色拥有权限
            if (roleAclList.contains(sysAcl)){
                sysAclDto.setChecked(true);
            }
            aclDtoList.add(sysAclDto);
        }
        return aclListToTree(aclDtoList);
    }

    public List<SysAclModuleDto> aclListToTree(List<SysAclDto> aclDtoList){
        if (aclDtoList == null){
            return new ArrayList<>();
        }
        List<SysAclModuleDto> sysAclModuleDtos = deptTree();
        ArrayListMultimap<Integer, SysAclDto> aclModuleAclMap = ArrayListMultimap.create();
        for (SysAclDto sysAclDto : aclDtoList){
            //判断权限状态
            if (sysAclDto.getStatus() == 1){
                aclModuleAclMap.put(sysAclDto.getAclModuleId(),sysAclDto);
            }
        }
        bindAcls(sysAclModuleDtos,aclModuleAclMap);
        //递归绑定权限点和权限模块
        return sysAclModuleDtos;
    }

    public void bindAcls(List<SysAclModuleDto> sysAclModuleDtos,ArrayListMultimap<Integer, SysAclDto> map){
        if (sysAclModuleDtos == null){
            return;
        }
        //遍历权限模块树 封装数据
        for (SysAclModuleDto dto : sysAclModuleDtos){
            //通过权限模块id从map中取出权限点集合
            List<SysAclDto> sysAclDtos = map.get(dto.getId());
            Collections.sort(sysAclDtos,new Compareble2());
            dto.setAclList(sysAclDtos);
            bindAcls(dto.getAclModuleList(),map);
        }
    }

    private class Compareble2 implements Comparator<SysAclDto> {
        @Override
        public int compare(SysAclDto o1, SysAclDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    }
}
