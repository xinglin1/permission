package com.xmcc.service;

import com.xmcc.beans.PageQuery;
import com.xmcc.model.SysAcl;
import com.xmcc.param.SysAclParam;
import org.springframework.stereotype.Service;

/**
 * @author 张兴林
 * @date 2019-03-26 16:44
 */

@Service
public interface SysAclService {

    PageQuery<SysAcl> pageAcl(int moduleId,PageQuery<SysAcl> pageQuery);

    void save(SysAclParam param);

    void update(SysAclParam param);

    void delete(Integer id);
}
