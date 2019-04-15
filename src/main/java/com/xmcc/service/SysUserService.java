package com.xmcc.service;

import com.xmcc.beans.PageQuery;
import com.xmcc.beans.TypeBean;
import com.xmcc.dao.SysUserMapper;
import com.xmcc.exception.ParamException;
import com.xmcc.model.SysUser;
import com.xmcc.param.SysUserParam;
import com.xmcc.param.UserParam;
import com.xmcc.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author 张兴林
 * @date 2019-03-22 11:13
 */
@Service
public class SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysLogService sysLogService;

    /**
     * 登录
     * @param param
     * @return
     */
    public SysUser login(UserParam param){
        BeanValidator.check(param);
        String username = param.getUsername();
        String password = param.getPassword();
        SysUser sysUser = sysUserMapper.findUserByUsername(username);
        String msg = "";
        if (sysUser == null){
            SysUser user = sysUserMapper.findUserByTel(username);
            if (user == null){
                msg = "该用户不存在";
            } else if (!user.getPassword().equals(MD5Util.encrypt(password))){
                msg = "密码错误";
            } else {
                return user;
            }
        } else if (!sysUser.getPassword().equals(MD5Util.encrypt(password))){
            msg = "密码错误";
        } else {
            return sysUser;
        }
//        throw new ParamException(msg);
        return null;
    }

    /**
     * 添加用户
     */
    @Transactional
    public void addUser(SysUserParam param){
        //校验参数
        BeanValidator.check(param);
        int i = rondomPassword();
        String password = MD5Util.encrypt(String.valueOf(i));
        SysUser sysUser = SysUser.builder().id(param.getId()).username(param.getUsername())
                            .telephone(param.getTelephone()).mail(param.getMail()).deptId(param.getDeptId()).
                                    status(param.getStatus()).password(password).remark(param.getRemark()).build();
        //获取用户名
        sysUser.setOperator(RequestHolder.getUser().getUsername());
        //获取用户ip
        sysUser.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
        sysUser.setOperateTime(new Date());
//        int insert = sysUserMapper.insert(sysUser);
        int id = sysUserMapper.insertSelective(sysUser);
        if (id == -1){
            throw new ParamException("添加失败,请充钱");
        }
        sysLogService.saveLog(TypeBean.SYS_USER,null,JsonMapper.obj2String(sysUser),id);
        MailUtils.sendMail(param.getMail(),"员工："+param.getUsername()+",欢迎你加入小码聪聪，你可以根据你的用户名或者电话号码登录本公司OA系统，" +
                        "你的登录密码是："+i, "欢迎你新人");
    }

    /**
     * 随机生成8位数用户密码
     */
    public int rondomPassword(){
        Random random = new Random();
        int i = (random.nextInt(89999999))+10000000;
        return i;
    }

    /**
     * 修改用户
     */
    public void update(SysUserParam param){
        //校验参数
        BeanValidator.check(param);
        SysUser sysUser1 = sysUserMapper.selectByPrimaryKey(param.getId());
        //todo:判断用户状态
        if (sysUser1 != null){

            SysUser sysUser = SysUser.builder().id(param.getId()).username(param.getUsername())
                    .telephone(param.getTelephone()).mail(param.getMail()).deptId(param.getDeptId()).
                            status(param.getStatus()).password(sysUser1.getPassword()).remark(param.getRemark()).build();
            //获取用户名
            sysUser.setOperator(RequestHolder.getUser().getUsername());
            //获取用户ip
            sysUser.setOperateIp(IpUtil.getUserIP(RequestHolder.getRequest()));
            sysUser.setOperateTime(new Date());
            int insert = sysUserMapper.updateByPrimaryKey(sysUser);
            if (insert == 0){
                throw new ParamException("修改失败,请充钱");
            } else {
                sysLogService.saveLog(TypeBean.SYS_USER,JsonMapper.obj2String(sysUser1),JsonMapper.obj2String(sysUser),param.getId());
            }
        } else {
            throw new ParamException("修改的用户不存在");
        }

    }

    /**
     * 分页
     */
    public PageQuery<SysUser> pageUser(int deptId,PageQuery<SysUser> pageQuery) {
        BeanValidator.check(pageQuery);
        int count = sysUserMapper.selectByDeptId(deptId);
        if (count > 0){
            PageQuery<SysUser> sysUserPageQuery = new PageQuery<>();
            List<SysUser> list = sysUserMapper.selectUserByDeptId(deptId,pageQuery);
            sysUserPageQuery.setData(list);
            sysUserPageQuery.setTotal(count);
            return sysUserPageQuery;
        }
        return new PageQuery<>();
    }
}
