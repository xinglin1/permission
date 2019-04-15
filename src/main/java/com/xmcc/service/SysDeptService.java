package com.xmcc.service;

import com.xmcc.beans.TypeBean;
import com.xmcc.dao.SysDeptMapper;
import com.xmcc.exception.ParamException;
import com.xmcc.model.SysDept;
import com.xmcc.param.DeptParam;
import com.xmcc.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 张兴林
 * @date 2019-03-21 17:29
 */

@Service
public class SysDeptService {

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private SysLogService sysLogService;

    /**
     * 更新部门
     * @param param
     */
    @Transactional
    public void save(DeptParam param){
        //参数校验
        BeanValidator.check(param);
        //验证是否存在相同部门
        int i = checkExist(param.getParentId(), param.getName(), param.getId());
        if (i > 0){
            throw new ParamException("添加的部门在当前层级下已存在");
        }
        //构建sysDept对象
        SysDept sysDept = SysDept.builder().name(param.getName()).parentId(param.getParentId())
                .remark(param.getRemark()).seq(param.getSeq()).build();
        sysDept.setLevel(LevelUtil.calculate(getLevel(param.getParentId()),param.getParentId()));
        sysDept.setOperator(RequestHolder.getUser().getUsername());
        sysDept.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
        sysDept.setOperateTime(new Date());
        int id = sysDeptMapper.insertSelective(sysDept);
        sysDept.setId(id);
        sysLogService.saveLog(TypeBean.SYS_DEPT,null,JsonMapper.obj2String(sysDept),id);
    }

    /**
     * 部门更新
     * 当更改上级部门时，对应的子孙级部门level发生改变
     * @param param
     */
    @Transactional
    public void update(DeptParam param){
        BeanValidator.check(param);
        //判断部门是否存在
        SysDept sysDept1 = sysDeptMapper.selectByPrimaryKey(param.getId());
//        int i = checkExist(param.getParentId(), param.getName(), param.getId());
        //如果部门存在
        if (sysDept1 != null){
            //获取父级部门
            SysDept sysDept3 = sysDeptMapper.selectByPrimaryKey(param.getParentId());
            //构建sysDept对象
            SysDept sysDept = SysDept.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId())
                    .remark(param.getRemark()).seq(param.getSeq()).level(LevelUtil.calculate(sysDept3.getLevel(),sysDept3.getId())).build();
            //获取用户名
            sysDept.setOperator(RequestHolder.getUser().getUsername());
            //获取用户ip
            sysDept.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
            sysDept.setOperateTime(new Date());

            List<SysDept> oldList = new ArrayList<>();
            List<SysDept> newList = new ArrayList<>();

            //如果存在，根据id查询旧的部门信息，与现有信息进行比较，判断是否更改parentId
            SysDept sysDept2 = sysDeptMapper.selectByPrimaryKey(param.getId());
            if (param.getParentId().equals(param.getId())){

                throw new ParamException("不能把父级部门更改为自己");

            } else if (!sysDept2.getParentId().equals(param.getParentId())){
                /**
                 * 根据id查询到所有子部门
                 * 遍历子部门所有数据，更改level值
                 */
                //List<SysDept> sonSysDept = sysDeptMapper.selectByParentId(param.getId());
                sysDeptMapper.updateByPrimaryKey(sysDept);
                oldList.add(sysDept2);
                newList.add(sysDept);
                updateLevelByParentId(sysDept.getId(),sysDept.getLevel(),oldList,newList);
            } else {
                oldList.add(sysDept2);
                newList.add(sysDept);
                //如果没有更改父级部门，就直接修改
                sysDeptMapper.updateByPrimaryKey(sysDept);
            }
//            sysLogService.saveLog(TypeBean.SYS_DEPT,JsonMapper.obj2String(oldList),JsonMapper.obj2String(newList),param.getId());
            System.out.println(oldList);
            System.out.println(newList);
            sysLogService.saveLog(TypeBean.SYS_DEPT,JsonMapper.obj2String(oldList),JsonMapper.obj2String(newList),param.getId());
        } else {
            throw new ParamException("更改的部门不存在");
        }
    }

    /**
     * 遍历子部门，更改level
     */
    public void updateLevelByParentId(int Id, String level, List<SysDept> oldList, List<SysDept> newList){
        List<SysDept> sonSysDept = sysDeptMapper.selectByParentId(Id);
        if (sonSysDept != null && sonSysDept.size() != 0){
            for (SysDept dept : sonSysDept) {
                //将遍历出来的部门信息进行备份，用于查询子部门的子部门
                SysDept copy = SysDept.builder().id(dept.getId()).name(dept.getName()).parentId(dept.getParentId())
                        .seq(dept.getSeq()).level(dept.getLevel()).remark(dept.getRemark()).operateIp(dept.getOperateIp())
                        .operator(dept.getOperator()).operateTime(dept.getOperateTime()).build();
                System.out.println("原本的"+dept);
                oldList.add(copy);
                //更改level值
                dept.setLevel(LevelUtil.calculate(level, Id));
                //根据parent_id更改level
                sysDeptMapper.updateByPrimaryKey(dept);
                System.out.println("现在的"+dept);
                newList.add(dept);

                updateLevelByParentId(dept.getId(),dept.getLevel(), oldList,newList);
            }
        } else {
            return;
        }

    }

    /**
     * 删除部门
     * @param id
     */
    @Transactional
    public void delete(Integer id){
        List<SysDept> deptList = sysDeptMapper.selectByParentId(id);
        int i = sysDeptMapper.countUserByDeptId(id);
        if (deptList == null || deptList.size() == 0){
            if (i > 0){
                throw new ParamException("不能删除存在用户的部门");
            } else {
                SysDept sysDept = sysDeptMapper.selectByPrimaryKey(id);
                sysDeptMapper.deleteByPrimaryKey(id);
                sysLogService.saveLog(TypeBean.SYS_DEPT, JsonMapper.obj2String(sysDept),null,id);
            }
        } else {
            throw new ParamException("不能删除拥有子部门的部门");
        }
    }

    //判断部门是否存在
    public int checkExist(Integer parentId,String deptName,Integer deptId){
        return sysDeptMapper.countByParentIdAndDeptNameAndDeptId(parentId,deptName,deptId);
    }

    //获取level
    public String getLevel(Integer deptId){
        SysDept sysDept = sysDeptMapper.selectByPrimaryKey(deptId);
        if (sysDept == null){
            return null;
        }
        return sysDept.getLevel();
    }

}
