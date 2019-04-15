package com.xmcc.dao;

import com.xmcc.beans.PageQuery;
import com.xmcc.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    SysUser findUserByUsername(@Param("username") String username);

    SysUser findUserByTel(@Param("telephone") String telephone);

    int selectByDeptId(@Param("deptId") int deptId);

    List<SysUser> selectUserByDeptId(@Param("deptId") int deptId,@Param("page") PageQuery<SysUser> pageQuery);

    List<SysUser> getAll();

    List<SysUser> selectUsersByUserIds(@Param("userIds") List<Integer> userIds);
}