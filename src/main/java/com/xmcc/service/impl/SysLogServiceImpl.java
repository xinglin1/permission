package com.xmcc.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xmcc.beans.PageQuery;
import com.xmcc.beans.SearchLog;
import com.xmcc.beans.TypeBean;
import com.xmcc.dao.*;
import com.xmcc.dto.SearchLogDto;
import com.xmcc.exception.ParamException;
import com.xmcc.model.*;
import com.xmcc.service.SysDeptService;
import com.xmcc.service.SysLogService;
import com.xmcc.utils.BeanValidator;
import com.xmcc.utils.IpUtil;
import com.xmcc.utils.JsonMapper;
import com.xmcc.utils.RequestHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 张兴林
 * @date 2019-04-02 09:33
 */
@Service
public class SysLogServiceImpl implements SysLogService {

    @Autowired
    private SysLogMapper sysLogMapper;

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private SysAclModuleMapper sysAclModuleMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysAclMapper sysAclMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleAclMapper sysRoleAclMapper;

    @Autowired
    private SysRoleUserMapper sysRoleUserMapper;

    /**
     * 保存日志：
     * 在修改部门的上级部门 或者权限模块的上级模块时，需要多次保存
     *
     * @param type
     */
    @Override
    public void saveLog(String type, String old, String now, Integer target_id) {
        int t = Integer.parseInt(type);
        SysLogWithBLOBs bs = new SysLogWithBLOBs();
        bs.setType(t);
        //如果操作前为空，说明执行的插入操作
        if (old == "" || old == null) {
            bs.setOldValue("");
        } else {
            bs.setOldValue(old);
            bs.setTargetId(target_id);
        }
        //执行删除操作时
        if (now == "" || now == null) {
            bs.setNewValue("");
        } else {
            bs.setNewValue(now);
            bs.setTargetId(target_id);
        }

        bs.setOperator(RequestHolder.getUser().getUsername());
        bs.setStatus(0);
        bs.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
        bs.setOperateTime(new Date());
        sysLogMapper.insert(bs);
    }

    @Override
    public PageQuery<SysLogWithBLOBs> search(SearchLog searchLog, PageQuery<SysLogWithBLOBs> pageBean) {
        BeanValidator.check(pageBean);
        SearchLogDto searchLogDto = new SearchLogDto();
        if (searchLog.getType() != null){
            searchLogDto.setType(searchLog.getType());
        }
        if (searchLog.getBeforeSeg() != null && searchLog.getBeforeSeg() != ""){
            searchLogDto.setBeforeSeg("%"+searchLog.getBeforeSeg()+"%");
        }
        if (searchLog.getAfterSeg() != null && searchLog.getAfterSeg() != ""){
            searchLogDto.setAfterSeg("%"+searchLog.getAfterSeg()+"%");
        }
        if (searchLog.getOperator() != null && searchLog.getOperator() != ""){
            searchLogDto.setOperator("%"+searchLog.getOperator()+"%");
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date fromDate = null;
        Date toTime = null;
        try {
            if (!searchLog.getFromTime().isEmpty()) {
                fromDate = format.parse(searchLog.getFromTime());
            }
            if (!searchLog.getToTime().isEmpty()) {
                toTime = format.parse(searchLog.getToTime());
            }
        } catch (ParseException e) {
            throw new ParamException("传入的日期格式有问题，只能为yyyy-MM-dd hh:mm:ss格式");
        }
        System.out.println(fromDate);
        searchLogDto.setFromTime(fromDate);
        searchLogDto.setToTime(toTime);
        List<SysLogWithBLOBs> logList = sysLogMapper.getLogBySearchLog(searchLogDto, pageBean);
        int count = sysLogMapper.countLogBySearchLog(searchLogDto, pageBean);
        pageBean.setData(logList);
        pageBean.setTotal(count);
        return pageBean;
    }

    /**
     * 恢复
     * @param id
     */
    @Override
    public void restore(Integer id){
        SysLogWithBLOBs log = sysLogMapper.selectByPrimaryKey(id);
        if (log.getNewValue().isEmpty() || log.getOldValue().isEmpty()){
            throw new ParamException("新增和删除操作的数据不能恢复");
        }
        String newValue = log.getOldValue();
        String oldValue = log.getNewValue();
        if (log.getStatus() == 0){
            switch (log.getType()){
                case 1:
                    List<SysDept> deptList = JsonMapper.string2Obj(newValue, new TypeReference<List<SysDept>>() {});
                    if (deptList != null){
                        for (SysDept dept :
                                deptList) {
                            System.out.println(dept);
                            sysDeptMapper.updateByPrimaryKey(dept);
                        }
                    }
                    break;
                case 2:
                    SysUser sysUser = JsonMapper.string2Obj(newValue, new TypeReference<SysUser>() {
                    });
                    if (sysUser != null){
                        sysUserMapper.updateByPrimaryKey(sysUser);
                    }
                    break;
                case 3:
                    SysAcl sysAcl = JsonMapper.string2Obj(newValue, new TypeReference<SysAcl>() {
                    });
                    if (sysAcl != null){
                        sysAclMapper.updateByPrimaryKey(sysAcl);
                    }
                    break;
                case 4:
                    List<SysAclModule> aclModuleList = JsonMapper.string2Obj(newValue, new TypeReference<List<SysAclModule>>() {});
                    if (aclModuleList != null){
                        for (SysAclModule aclModule :
                                aclModuleList) {
                            System.out.println(aclModule);
                            sysAclModuleMapper.updateByPrimaryKey(aclModule);
                        }
                    }
                    break;
                case 5:
                    SysRole sysRole = JsonMapper.string2Obj(newValue, new TypeReference<SysRole>() {
                    });
                    if (sysRole != null){
                        sysRoleMapper.updateByPrimaryKey(sysRole);
                    }
                    break;
                case 6:
                    List<Integer> acls = JsonMapper.string2Obj(newValue, new TypeReference<List<Integer>>() {
                    });
                    Integer roleId = log.getTargetId();
                    List<SysRoleAcl> sysRoleAclList =  sysRoleAclMapper.selectByRoleId(roleId);
                    if (sysRoleAclList != null) {
                        sysRoleAclMapper.deleteByRoleId(roleId);
                        for (int i = 0; i < acls.size(); i++) {
                            SysRoleAcl sysRoleAcl = SysRoleAcl.builder().id(sysRoleAclList.get(0).getId()).aclId(acls.get(i))
                                    .roleId(roleId).operateIp(sysRoleAclList.get(0).getOperateIp()).operateTime(sysRoleAclList.get(0).getOperateTime())
                                    .operator(sysRoleAclList.get(0).getOperator()).build();
                            sysRoleAclMapper.updateByPrimaryKey(sysRoleAcl);
                        }
                    }
                    break;
                case 7:
                    List<Integer> userIds = JsonMapper.string2Obj(newValue, new TypeReference<List<Integer>>() {
                    });
                    Integer roleid = log.getTargetId();
                    List<SysRoleUser> sysRoleUserList = sysRoleUserMapper.selectByRoleId(roleid);
                    if (sysRoleUserList != null) {
                        sysRoleAclMapper.deleteByRoleId(roleid);
                        for (int i = 0; i < userIds.size(); i++) {
                            SysRoleUser sysRoleUser = SysRoleUser.builder().id(sysRoleUserList.get(0).getId()).userId(userIds.get(i))
                                    .roleId(roleid).operator(sysRoleUserList.get(0).getOperator()).operateTime(sysRoleUserList.get(0).getOperateTime())
                                    .operateIp(sysRoleUserList.get(0).getOperateIp()).build();
                            sysRoleUserMapper.updateByPrimaryKey(sysRoleUser);
                        }
                    }
                    break;
            }
            log.setStatus(1);
            sysLogMapper.updateByPrimaryKey(log);
            saveLog(TypeBean.SYS_DEPT,oldValue,newValue,log.getTargetId());
        } else {
            throw new ParamException("此条记录已经被恢复过，不能再次恢复");
        }

    }

}
