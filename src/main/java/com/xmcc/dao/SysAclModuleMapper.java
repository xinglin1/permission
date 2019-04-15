package com.xmcc.dao;

import com.xmcc.model.SysAclModule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclModuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAclModule record);

    int insertSelective(SysAclModule record);

    SysAclModule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAclModule record);

    int updateByPrimaryKey(SysAclModule record);

    List<SysAclModule> findAllModule();

    int countByParentIdAndNameAndId(@Param("parentId") Integer parentId,@Param("name") String name,@Param("id") Integer id);

    List<SysAclModule> selectByParentId(@Param("parentId") int id);

    int countAclByModuleId(@Param("id") Integer id);
}