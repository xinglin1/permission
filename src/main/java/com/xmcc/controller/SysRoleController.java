package com.xmcc.controller;

import com.xmcc.dto.SysAclModuleDto;
import com.xmcc.model.SysRole;
import com.xmcc.param.SysRoleParam;
import com.xmcc.service.SysModuleTreeService;
import com.xmcc.service.SysRoleService;
import com.xmcc.utils.ConversionUtil;
import com.xmcc.utils.JsonData;
import com.xmcc.utils.JsonMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * @author 张兴林
 * @date 2019-03-26 09:10
 */

/**
 * 角色模块
 */
@Controller
@RequestMapping("/sys/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysModuleTreeService sysModuleTreeService;

    @RequestMapping("/role.page")
    public ModelAndView roleManage() {
        return new ModelAndView("role");
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public JsonData listRole() {
        List<SysRole> list = sysRoleService.selectAllRole();
        return JsonData.success(list);
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(SysRoleParam param) {
        int i = sysRoleService.save(param);
        if (i > 0){
            return JsonData.success();
        } else {
            return JsonData.fail("添加失败，请联系管理员");
        }
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData update(SysRoleParam param) {
        int i = sysRoleService.update(param);
        if (i > 0){
            return JsonData.success();
        } else {
            return JsonData.fail("修改失败，请联系管理员");
        }

    }

    @RequestMapping("/delete.json")
    @ResponseBody
    public JsonData delete(Integer id) {
        int i = sysRoleService.delete(id);
        if (i > 0){
            return JsonData.success();
        } else {
            return JsonData.fail("删除失败");
        }
    }

    @RequestMapping("/roleTree.json")
    @ResponseBody
    public JsonData roleTree(Integer roleId) {
        List<SysAclModuleDto> sysAclModuleDtos = sysModuleTreeService.roleTree(roleId);
        System.out.println(JsonMapper.obj2String(sysAclModuleDtos));
        return JsonData.success(sysAclModuleDtos);
    }

    @RequestMapping("/changeAcls.json")
    @ResponseBody
    public JsonData changeAcls(Integer roleId, String aclIds) {
        sysRoleService.changeAcls(roleId,aclIds);
        return JsonData.success();
    }

    @RequestMapping("/users.json")
    @ResponseBody
    public JsonData users(Integer roleId){
        Map map = sysRoleService.users(roleId);
        return JsonData.success(map);
    }

    @RequestMapping("/changeUsers.json")
    @ResponseBody
    public JsonData changeUsers(@Param("roleId") Integer roleId, String userIds){
        List<Integer> userIdList = ConversionUtil.string2List(userIds);
        sysRoleService.changeUsers(roleId,userIdList);
        return JsonData.success();
    }
}
