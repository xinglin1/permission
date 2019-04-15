package com.xmcc.service.impl;

import com.xmcc.beans.PageQuery;
import com.xmcc.beans.TypeBean;
import com.xmcc.dao.SysAclMapper;
import com.xmcc.exception.ParamException;
import com.xmcc.model.SysAcl;
import com.xmcc.model.SysUser;
import com.xmcc.param.SysAclParam;
import com.xmcc.service.SysAclService;
import com.xmcc.service.SysLogService;
import com.xmcc.utils.BeanValidator;
import com.xmcc.utils.IpUtil;
import com.xmcc.utils.JsonMapper;
import com.xmcc.utils.RequestHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author 张兴林
 * @date 2019-03-26 16:49
 */
@Service
public class SysAclServiceImpl implements SysAclService {

    @Autowired
    private SysAclMapper sysAclMapper;

    @Autowired
    private SysLogService sysLogService;

    @Override
    public PageQuery<SysAcl> pageAcl(int moduleId, PageQuery<SysAcl> pageQuery) {
        BeanValidator.check(pageQuery);
        int count = sysAclMapper.selectByModuleId(moduleId);
        if (count > 0) {
            PageQuery<SysAcl> sysAclPageQuery = new PageQuery<>();
            List<SysAcl> list = sysAclMapper.selectAclByModuleId(moduleId, pageQuery);
            sysAclPageQuery.setData(list);
            sysAclPageQuery.setTotal(count);
            return sysAclPageQuery;
        }
        return new PageQuery<>();
    }

    @Override
    public void save(SysAclParam param) {
        BeanValidator.check(param);
        int i = checkAcl(param.getAclModuleId(), param.getName(), param.getId());
        if (i > 0) {
            throw new ParamException("当前模块下已存此权限点");
        } else {
            SysAcl sysAcl = SysAcl.builder().id(param.getId()).name(param.getName()).aclModuleId(param.getAclModuleId())
                    .url(param.getUrl()).type(param.getType()).status(param.getStatus()).seq(param.getSeq()).remark(param.getRemark())
                    .operateIp(IpUtil.getUserIP(RequestHolder.getRequest())).operator(RequestHolder.getUser().getUsername())
                    .build();
            Date date = new Date();
            sysAcl.setOperateTime(date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
            String format = simpleDateFormat.format(date);
            Random random = new Random();
            int i1 = random.nextInt(100);
            sysAcl.setCode(format+i1);
            int id = sysAclMapper.insertSelective(sysAcl);
            if (id == -1){
                throw new ParamException("添加失败，请联系管理员");
            }
            sysLogService.saveLog(TypeBean.SYS_ACL, null,JsonMapper.obj2String(sysAcl),id);
        }
    }

    @Override
    public void update(SysAclParam param) {
        BeanValidator.check(param);
        int i = checkAcl(param.getAclModuleId(), param.getName(), param.getId());
        SysAcl sysAcl1 = sysAclMapper.selectByPrimaryKey(param.getId());
        if (sysAcl1 != null){
            if (i > 0){
                throw new ParamException("修改的名字在当前模块已存在");
            }
            SysAcl sysAcl = new SysAcl();
            BeanUtils.copyProperties(param,sysAcl);
            sysAcl.setCode(sysAcl1.getCode());
            sysAcl.setOperateTime(new Date());
            sysAcl.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
            sysAcl.setOperator(RequestHolder.getUser().getUsername());
            int i1 = sysAclMapper.updateByPrimaryKey(sysAcl);
            if (i1 < 1){
                throw new ParamException("修改失败，请联系管理员");
            }
            sysLogService.saveLog(TypeBean.SYS_ACL, JsonMapper.obj2String(sysAcl1),JsonMapper.obj2String(sysAcl),param.getId());
        } else {
            throw new ParamException("修改的权限点已被删除");
        }
    }

    @Override
    public void delete(Integer id) {
        SysAcl sysAcl = sysAclMapper.selectByPrimaryKey(id);
        if (sysAcl != null){
            int i = sysAclMapper.deleteByPrimaryKey(id);
            if (i != 0){
                sysLogService.saveLog(TypeBean.SYS_ACL, JsonMapper.obj2String(sysAcl),null,id);
                return;
            } else {
                throw new ParamException("删除失败");
            }
        } else {
            throw new ParamException("该权限点已被删除");
        }
    }

    public int checkAcl(Integer aclModuleId, String name, Integer id) {
        return sysAclMapper.countAclByAclModuleIdAndNameAndId(aclModuleId, name, id);
    }
}
