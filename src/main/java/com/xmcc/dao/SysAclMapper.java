package com.xmcc.dao;

import com.xmcc.beans.PageQuery;
import com.xmcc.model.SysAcl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAcl record);

    int insertSelective(SysAcl record);

    SysAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAcl record);

    int updateByPrimaryKey(SysAcl record);

    int selectByModuleId(@Param("moduleId") int moduleId);

    List<SysAcl> selectAclByModuleId(@Param("moduleId") int moduleId,@Param("pageQuery") PageQuery<SysAcl> pageQuery);

    int countAclByAclModuleIdAndNameAndId(@Param("aclModuleId") Integer aclModuleId,@Param("name") String name,@Param("id") Integer id);

    List<SysAcl> getByIdList(@Param("aclIdList") List<Integer> aclIdList);

    List<SysAcl> getAll();

    SysAcl getSysAclByUrl(@Param("url") String uri);
}