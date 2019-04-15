package com.xmcc.service.impl;

import com.xmcc.beans.CacheKeyPrefix;
import com.xmcc.beans.TypeBean;
import com.xmcc.dao.*;
import com.xmcc.exception.ParamException;
import com.xmcc.model.*;
import com.xmcc.param.SysRoleParam;
import com.xmcc.service.SysCacheService;
import com.xmcc.service.SysCoreService;
import com.xmcc.service.SysLogService;
import com.xmcc.service.SysRoleService;
import com.xmcc.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author 张兴林
 * @date 2019-03-26 20:27
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleAclMapper sysRoleAclMapper;

    @Autowired
    private SysRoleUserMapper sysRoleUserMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysAclMapper sysAclMapper;

    @Autowired
    private SysCacheService sysCacheService;

    @Autowired
    private SysLogService sysLogService;

    @Override
    public List<SysRole> selectAllRole() {
        List<SysRole> list = sysRoleMapper.selectAllRole();
        return list;
    }

    /**
     * 新增角色
     * @param param
     * @return
     */
    @Override
    public int save(SysRoleParam param) {
        BeanValidator.check(param);
        int i = checkRole(param.getName(), param.getId());
        if (i > 0){
            throw new ParamException("新增角色已存在");
        }
        SysRole sysRole = SysRole.builder().name(param.getName()).remark(param.getRemark())
                .status(param.getStatus()).type(param.getType()).build();
        sysRole.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
        sysRole.setOperateTime(new Date());
        sysRole.setOperator(RequestHolder.getUser().getUsername());
        int id = sysRoleMapper.insertSelective(sysRole);
        sysLogService.saveLog(TypeBean.SYS_ROLE,null,JsonMapper.obj2String(sysRole),id);
        return id;
    }

    /**
     * 修改角色
     * @param param
     * @return
     */
    @Override
    public int update(SysRoleParam param) {
        BeanValidator.check(param);
        SysRole sysRole = sysRoleMapper.selectByPrimaryKey(param.getId());
        if (sysRole != null){
            int i = checkRole(param.getName(), param.getId());
            if (i > 0){
                throw new ParamException("修改的角色名已经存在");
            } else {
                SysRole sysRole1 = SysRole.builder().id(sysRole.getId()).name(param.getName())
                        .remark(param.getRemark()).status(param.getStatus()).type(param.getType()).build();
                sysRole1.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
                sysRole1.setOperateTime(new Date());
                sysRole1.setOperator(RequestHolder.getUser().getUsername());
                int i1 = sysRoleMapper.updateByPrimaryKey(sysRole1);
                if (i1 > 0){
                    sysLogService.saveLog(TypeBean.SYS_ROLE,JsonMapper.obj2String(sysRole),JsonMapper.obj2String(sysRole1),param.getId());
                }
                return i1;
            }
        } else {
            throw new ParamException("修改的角色不存在");
        }
    }

    /**
     * 删除角色
     * @param id
     * @return
     */
    @Override
    public int delete(Integer id) {
        SysRole sysRole = sysRoleMapper.selectByPrimaryKey(id);
        if (sysRole != null){
            int i = sysRoleMapper.deleteByPrimaryKey(id);
            if (i > 0){
                sysLogService.saveLog(TypeBean.SYS_ROLE,JsonMapper.obj2String(sysRole),null,sysRole.getId());
            }
            return i;
        } else {
            return 0;
        }
    }

    @Override
    @Transactional
    public void changeAcls(Integer roleId, String aclIds) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(roleId);
        List<Integer> dbAclIds = sysRoleAclMapper.getAclIdListByRoleIdList(list);
        if (aclIds.length() == 0){
            sysLogService.saveLog(TypeBean.SYS_ROLE_ACL,JsonMapper.obj2String(dbAclIds),null,roleId);
            int i = sysRoleAclMapper.deleteByRoleId(roleId);
        } else {
            String s = dbAclIds.toString();
            if (s.length() != aclIds.length() || !aclIds.equals(s)){
                //如果长度不同或者内容不同，先删除所有，再依次添加
                sysRoleAclMapper.deleteByRoleId(roleId);
                String[] split = aclIds.split(",");
                List<SysRoleAcl> sysRoleAcls = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    SysRoleAcl sysRoleAcl = new SysRoleAcl();
                    sysRoleAcl.setRoleId(roleId);
                    sysRoleAcl.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
                    sysRoleAcl.setOperateTime(new Date());
                    sysRoleAcl.setOperator(RequestHolder.getUser().getUsername());
                    sysRoleAcl.setAclId(Integer.valueOf(split[i]));
                    sysRoleAcls.add(sysRoleAcl);
                }
                System.out.println(sysRoleAcls.toString());
                sysRoleAclMapper.bathInsert(sysRoleAcls);

                sysLogService.saveLog(TypeBean.SYS_ROLE_ACL,JsonMapper.obj2String(dbAclIds),JsonMapper.obj2String(ConversionUtil.string2List(aclIds)),roleId);

                changeCache(roleId,aclIds);
            } else {
                return;
            }
        }
    }

    //改变缓存数据
    private void changeCache(Integer roleId, String aclIds){
        List<Integer> aclIdList = ConversionUtil.string2List(aclIds);
        //根据aclIds查询出所有acl
        List<SysAcl> sysAcls = sysAclMapper.getByIdList(aclIdList);
        List<Integer> userIdList = sysRoleUserMapper.getUserIdListByRoleId(roleId);
        for (int i = 0; i < userIdList.size(); i++) {
            sysCacheService.saveCache(JsonMapper.obj2String(sysAcls),24*60*60,String.valueOf(userIdList.get(i)),CacheKeyPrefix.USER_ACLS);
        }
    }

    @Override
    public Map users(Integer roleId) {
        //根据roleId查询userId
        List<Integer> userIds = sysRoleUserMapper.getUserIdListByRoleId(roleId);
        //查询所有user信息
        List<SysUser> allUser = sysUserMapper.getAll();
        List<SysUser> selectUser = new ArrayList<>();
        Map<String, List<SysUser>> map = new HashMap<>();
        //如果userIds为空，将select设置为空
        if (userIds.size() == 0){
            map.put("selected",new ArrayList<>());
        } else {
            //根据userIds查询user信息
            selectUser = sysUserMapper.selectUsersByUserIds(userIds);
            map.put("selected",selectUser);
        }
        boolean b = allUser.removeAll(selectUser);
        map.put("unselected",allUser);
        return map;
    }

    @Override
    @Transactional
    public void changeUsers(Integer roleId, List<Integer> userIdList) {
        //从数据库根据roleId查询所有userId
        List<Integer> dbUserIdList = sysRoleUserMapper.getUserIdListByRoleId(roleId);
        //如果集合长度为0，则直接删除数据
        if (userIdList.size() == 0){
            int i = sysRoleUserMapper.deleteByRoleId(roleId);
            sysLogService.saveLog(TypeBean.SYS_ROLE_USER,JsonMapper.obj2String(dbUserIdList),null,roleId);
            return;
        }
        if (userIdList.size() == dbUserIdList.size()){
            boolean b = userIdList.removeAll(dbUserIdList);
            if (b){
                return;
            }
        }
        List<SysRoleUser> sysRoleUserList = new ArrayList<>();
        for (int i = 0; i < userIdList.size(); i++) {
            SysRoleUser sysRoleUser = SysRoleUser.builder().roleId(roleId).userId(userIdList.get(i))
                    .operateIp(IpUtil.getUserIP(RequestHolder.getRequest()))
                    .operateTime(new Date())
                    .operator(RequestHolder.getUser().getUsername()).build();
            sysRoleUserList.add(sysRoleUser);
        }
        //删除数据，再添加
        sysRoleUserMapper.deleteByRoleId(roleId);
        sysRoleUserMapper.batchInsert(sysRoleUserList);

        sysLogService.saveLog(TypeBean.SYS_ROLE_USER,JsonMapper.obj2String(dbUserIdList),JsonMapper.obj2String(userIdList),roleId);

        changeCache(userIdList);
    }

    //改变缓存数据
    private void changeCache(List<Integer> userIdList){
        for (int i = 0; i < userIdList.size(); i++) {
            //根据userId从数据库查询对应的roleId
            List<Integer> roleIdList = sysRoleUserMapper.getRoleIdListByUserId(userIdList.get(i));
            //根据roleId查询aclId
            List<Integer> aclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(roleIdList);
            //根据aclId查询acl
            List<SysAcl> sysAcls = sysAclMapper.getByIdList(aclIdList);
            sysCacheService.saveCache(JsonMapper.obj2String(sysAcls),24*60*60,String.valueOf(userIdList.get(i)),CacheKeyPrefix.USER_ACLS);
        }
    }

    public int checkRole(String name, Integer id) {
        return sysRoleMapper.countAclByAclModuleIdAndNameAndId(name, id);
    }
}
