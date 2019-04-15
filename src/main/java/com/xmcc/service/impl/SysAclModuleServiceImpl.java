package com.xmcc.service.impl;

import com.xmcc.beans.TypeBean;
import com.xmcc.dao.SysAclModuleMapper;
import com.xmcc.exception.ParamException;
import com.xmcc.model.SysAclModule;
import com.xmcc.param.SysAclModuleParam;
import com.xmcc.service.SysAclModuleService;
import com.xmcc.service.SysLogService;
import com.xmcc.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 张兴林
 * @date 2019-03-26 09:54
 */
@Service
public class SysAclModuleServiceImpl implements SysAclModuleService {

    @Autowired
    private SysAclModuleMapper sysAclModuleMapper;

    @Autowired
    private SysLogService sysLogService;

    /**
     * 新增
     * @param param
     */
    @Override
    public void save(SysAclModuleParam param) {
        //参数校验
        BeanValidator.check(param);
        //验证是否存在相同部门
        int i = checkExist(param.getParentId(), param.getName(), param.getId());
        if (i > 0){
            throw new ParamException("添加的权限在当前层级下已存在");
        }
        SysAclModule sysAclModule = SysAclModule.builder().id(param.getId()).name(param.getName())
                .status(param.getStatus()).level(LevelUtil.calculate(getLevel(param.getParentId()),param.getParentId()))
                .remark(param.getRemark()).parentId(param.getParentId()).seq(param.getSeq()).build();
        sysAclModule.setOperator(RequestHolder.getUser().getUsername());
        sysAclModule.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
        sysAclModule.setOperateTime(new Date());
        int insert = sysAclModuleMapper.insertSelective(sysAclModule);
        if (insert < 1){
            throw new ParamException("参数异常");
        }
        sysLogService.saveLog(TypeBean.SYS_ACL_MODULE,null,JsonMapper.obj2String(sysAclModule),insert);
//        BeanUtils.copyProperties(param,sysAclModule);
    }

    /**
     * 修改
     * @param param
     */
    @Override
    public void update(SysAclModuleParam param){
        BeanValidator.check(param);
        SysAclModule sysAclModule = sysAclModuleMapper.selectByPrimaryKey(param.getId());
        if (sysAclModule != null){
            SysAclModule sysAclModule3 = sysAclModuleMapper.selectByPrimaryKey(param.getParentId());

            //构建sysAclModule对象
            SysAclModule sysAclModule1 = SysAclModule.builder().id(sysAclModule.getId()).name(param.getName()).parentId(param.getParentId())
                    .level(LevelUtil.calculate(getLevel(param.getParentId()),param.getParentId())).seq(param.getSeq())
                    .status(param.getStatus()).remark(param.getRemark()).build();

            sysAclModule1.setOperator(RequestHolder.getUser().getUsername());
            sysAclModule1.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
            sysAclModule1.setOperateTime(new Date());

            List<SysAclModule> oldList = new ArrayList<>();
            List<SysAclModule> newList = new ArrayList<>();

            SysAclModule sysAclModule2 = sysAclModuleMapper.selectByPrimaryKey(param.getId());
            if (param.getParentId().equals(param.getId())){

                throw new ParamException("不能把上级模块更改为自己");

            }else if (!sysAclModule2.getParentId().equals(param.getParentId())){
                /**
                 * 根据id查询到所有子
                 * 遍历子所有数据，更改level值
                 */
                sysAclModuleMapper.updateByPrimaryKey(sysAclModule1);
                oldList.add(sysAclModule);
                newList.add(sysAclModule1);
                updateLevelByParentId(sysAclModule1.getId(),sysAclModule1.getLevel(),oldList,newList);
            } else {
                //如果没有更改父级部门，就直接修改
                sysAclModuleMapper.updateByPrimaryKey(sysAclModule1);
            }
        }else {
            throw new ParamException("修改的角色不存在");
        }
    }

    @Override
    public void delete(Integer id){
        SysAclModule sysAclModule = sysAclModuleMapper.selectByPrimaryKey(id);
        if (sysAclModule != null){
            List<SysAclModule> moduleList = sysAclModuleMapper.selectByParentId(id);
            if (moduleList != null && moduleList.size() > 0){
                throw new ParamException("不能删除有子模块的模块");
            } else {
                int i = sysAclModuleMapper.countAclByModuleId(id);
                if (i > 0){
                    throw new ParamException("不能删除拥有权限点的权限模块");
                } else {
                    int i1 = sysAclModuleMapper.deleteByPrimaryKey(id);
                    if (i1 < 1){
                        throw new ParamException("系统错误，请充钱");
                    }
                    sysLogService.saveLog(TypeBean.SYS_ACL_MODULE, JsonMapper.obj2String(sysAclModule),null,id);
                }
            }
        } else {
            throw new ParamException("删除的部门不存在");
        }
    }

    /**
     * 遍历子权限，更改level
     */
    public void updateLevelByParentId(int Id, String level, List<SysAclModule> oldList, List<SysAclModule> newList){
        List<SysAclModule> sonSysAclModule = sysAclModuleMapper.selectByParentId(Id);
        if (sonSysAclModule != null && sonSysAclModule.size() != 0) {
            for (SysAclModule sysAclModule : sonSysAclModule) {
                //将遍历出来的部门信息进行备份，用于查询子部门的子部门
                SysAclModule copyModule = SysAclModule.builder().id(sysAclModule.getId()).level(sysAclModule.getLevel())
                        .name(sysAclModule.getName()).parentId(sysAclModule.getParentId()).remark(sysAclModule.getRemark())
                        .seq(sysAclModule.getSeq()).status(sysAclModule.getStatus()).operateIp(sysAclModule.getOperateIp())
                        .operateTime(sysAclModule.getOperateTime()).operator(sysAclModule.getOperator()).build();
                oldList.add(copyModule);
                //更改level值
                sysAclModule.setLevel(LevelUtil.calculate(level, Id));
                //根据parent_id更改level
                sysAclModuleMapper.updateByPrimaryKey(sysAclModule);
                newList.add(sysAclModule);
                updateLevelByParentId(copyModule.getId(),sysAclModule.getLevel(), oldList, newList);
            }
        } else {
            return;
        }

    }

    private int checkExist(Integer parentId, String name, Integer id) {
        return sysAclModuleMapper.countByParentIdAndNameAndId(parentId,name,id);
    }

    //获取level
    public String getLevel(Integer id){
        SysAclModule sysAclModule = sysAclModuleMapper.selectByPrimaryKey(id);
        if (sysAclModule == null){
            return null;
        }
        return sysAclModule.getLevel();
    }
}
