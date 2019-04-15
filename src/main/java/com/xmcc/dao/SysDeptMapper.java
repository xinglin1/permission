package com.xmcc.dao;

import com.xmcc.model.SysDept;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SysDeptMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysDept record);

    int insertSelective(SysDept record);

    SysDept selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysDept record);

    int updateByPrimaryKey(SysDept record);

    int countByParentIdAndDeptNameAndDeptId(@Param("parentId") Integer parentId,@Param("deptName") String deptName,@Param("deptId") Integer deptId);

    List<SysDept> findAllDept();

    List<SysDept> selectByParentId(@Param("parentId") Integer id);

    int countUserByDeptId(@Param("id") Integer id);

}