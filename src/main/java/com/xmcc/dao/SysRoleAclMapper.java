package com.xmcc.dao;

import com.xmcc.model.SysRole;
import com.xmcc.model.SysRoleAcl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleAcl record);

    int insertSelective(SysRoleAcl record);

    SysRoleAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleAcl record);

    int updateByPrimaryKey(SysRoleAcl record);

    List<Integer> getAclIdListByRoleIdList(@Param("roleIdList") List<Integer> roleIdList);

    int deleteByRoleId(@Param("roleId") Integer roleId);

    void bathInsert(@Param("sysRoleAcls") List<SysRoleAcl> sysRoleAcls);

    void updateByRoleId(@Param("roleId") Integer roleId,@Param("aclId") Integer aclId);

    List<SysRoleAcl> selectByRoleId(@Param("roleId") Integer roleId);
}