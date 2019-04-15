package com.xmcc.service;

import com.xmcc.model.SysAcl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SysCoreService {

    List<SysAcl> getUserAclList();

    List<SysAcl> getRoleAclList(Integer roleId);

    boolean hasAcl(String uri);
}
