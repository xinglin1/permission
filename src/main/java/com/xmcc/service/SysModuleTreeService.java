package com.xmcc.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.xmcc.dao.SysAclModuleMapper;
import com.xmcc.dto.SysAclModuleDto;
import com.xmcc.model.SysAclModule;
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
public interface SysModuleTreeService {

    List<SysAclModuleDto> deptTree();

    List<SysAclModuleDto> deptListToTree(List<SysAclModuleDto> sysAclModuleDtoList);

    void DeptTree(List<SysAclModuleDto> rootList,Multimap<String,SysAclModuleDto> multimap);

    List<SysAclModuleDto> roleTree(Integer roleId);
}
