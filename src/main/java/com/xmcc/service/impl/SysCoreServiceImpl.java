package com.xmcc.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xmcc.beans.CacheKeyPrefix;
import com.xmcc.dao.SysAclMapper;
import com.xmcc.dao.SysRoleAclMapper;
import com.xmcc.dao.SysRoleUserMapper;
import com.xmcc.model.SysAcl;
import com.xmcc.model.SysUser;
import com.xmcc.service.SysCacheService;
import com.xmcc.service.SysCoreService;
import com.xmcc.utils.JsonMapper;
import com.xmcc.utils.RequestHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张兴林
 * @date 2019-03-27 16:54
 */
@Service
public class SysCoreServiceImpl implements SysCoreService {

    @Autowired
    private SysRoleAclMapper sysRoleAclMapper;

    @Autowired
    private SysAclMapper sysAclMapper;

    @Autowired
    private SysRoleUserMapper sysRoleUserMapper;

    @Autowired
    private SysCacheService sysCacheService;

    @Override
    public List<SysAcl> getUserAclList(){
        //获取userId
        Integer id = RequestHolder.getUser().getId();
        //判断是否是超管
        if (isSuperAdmin()){
            return sysAclMapper.getAll();
        }
        //通过用户id获取用户拥有的角色
        //先从缓存中读取
        String value = sysCacheService.getInfoFromCache(String.valueOf(id), CacheKeyPrefix.USER_ACLS);
        if (value != "" && value != null){
            TypeReference<List<SysAcl>> typeReference = new TypeReference<List<SysAcl>>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            };
            List<SysAcl> sysAcls = JsonMapper.string2Obj(value, typeReference);
            System.out.println("读取缓存数据");
            return sysAcls;
        }
        List<Integer> userRoleIdList = sysRoleUserMapper.getRoleIdListByUserId(id);
        if (userRoleIdList == null || userRoleIdList.size() == 0){
            return new ArrayList<>();
        }

        //通过角色id查询权限点id
        List<Integer> userAclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(userRoleIdList);
        if (userAclIdList.size() != 0){
            //通过权限点id查询权限对象
            List<SysAcl> userAclList = sysAclMapper.getByIdList(userAclIdList);
            sysCacheService.saveCache(JsonMapper.obj2String(userAclList),24*60*60,String.valueOf(id),CacheKeyPrefix.USER_ACLS);
            System.out.println("存入数据到缓存");
            return userAclList;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 根据 roleId 获取当前角色拥有的权限
     * @param roleId
     * @return
     */
    @Override
    public List<SysAcl> getRoleAclList(Integer roleId){
        List<Integer> roleIdList = new ArrayList<>();
        roleIdList.add(roleId);
        List<Integer> aclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(roleIdList);
        //如果获取到的aclId等于null或者长度为0，就返回空集合
        if (aclIdList == null || aclIdList.size() == 0){
            return new ArrayList<>();
        }
        return sysAclMapper.getByIdList(aclIdList);
    }

    @Override
    public boolean hasAcl(String uri) {
        if (isSuperAdmin()){
            return true;
        }
        List<SysAcl> userAclList = getUserAclList();
        System.out.println(userAclList.toString());
        SysAcl sysAcl = sysAclMapper.getSysAclByUrl(uri);
        if (sysAcl == null){
            return true;
        }
        //如果拥有权限，返回true
        if (userAclList.contains(sysAcl)){
            return true;
        }
        return false;
    }

    /**
     * 判断是否是管理员
     * @return
     */
    public boolean isSuperAdmin(){
        SysUser user = RequestHolder.getUser();
        if (user.getUsername().contains("Admin")){
            return true;
        }
        return false;
    }

}
