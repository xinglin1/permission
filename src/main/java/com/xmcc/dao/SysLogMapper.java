package com.xmcc.dao;

import com.xmcc.beans.PageQuery;
import com.xmcc.beans.SearchLog;
import com.xmcc.dto.SearchLogDto;
import com.xmcc.model.SysLog;
import com.xmcc.model.SysLogWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysLogWithBLOBs record);

    int insertSelective(SysLogWithBLOBs record);

    SysLogWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysLogWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(SysLogWithBLOBs record);

    int updateByPrimaryKey(SysLog record);

    List<SysLogWithBLOBs> getLogBySearchLog(@Param("searchLog") SearchLogDto searchLog, @Param("pageBean") PageQuery<SysLogWithBLOBs> pageBean);

    int countLogBySearchLog(@Param("searchLog") SearchLogDto searchLog,@Param("pageBean") PageQuery<SysLogWithBLOBs> pageBean);
}