package com.xmcc.dao;

import com.xmcc.model.SysRoleAcl;
import com.xmcc.model.SysRoleUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleUser record);

    int insertSelective(SysRoleUser record);

    SysRoleUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleUser record);

    int updateByPrimaryKey(SysRoleUser record);

    List<Integer> getRoleIdListByUserId(@Param("userId") Integer id);

    List<Integer> getUserIdListByRoleId(@Param("roleId") Integer roleId);

    int deleteByRoleId(@Param("roleId") Integer roleId);

    void batchInsert(@Param("sysRoleUserList") List<SysRoleUser> sysRoleUserList);

    List<SysRoleUser> selectByRoleId(@Param("roleId") Integer roleid);
}