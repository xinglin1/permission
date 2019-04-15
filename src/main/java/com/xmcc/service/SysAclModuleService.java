package com.xmcc.service;

import com.xmcc.param.SysAclModuleParam;
import org.springframework.stereotype.Service;

@Service
public interface SysAclModuleService {

    void save(SysAclModuleParam param);

    void update(SysAclModuleParam param);

    void delete(Integer id);
}
