package com.xmcc.service;

import com.xmcc.model.SysRole;
import com.xmcc.param.SysRoleParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 张兴林
 * @date 2019-03-26 20:27
 */
@Service
public interface SysRoleService {
    List<SysRole> selectAllRole();

    int save(SysRoleParam param);

    int update(SysRoleParam param);

    int delete(Integer id);

    void changeAcls(Integer roleId, String aclIds);

    Map users(Integer roleId);

    void changeUsers(Integer roleId, List<Integer> userIdList);
}
