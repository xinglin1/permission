package com.xmcc.controller;

import com.xmcc.dto.SysAclModuleDto;
import com.xmcc.param.SysAclModuleParam;
import com.xmcc.service.SysAclModuleService;
import com.xmcc.service.SysModuleTreeService;
import com.xmcc.service.impl.SysModuleTreeServiceImpl;
import com.xmcc.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author 张兴林
 * @date 2019-03-26 09:05
 */

/**
 * 权限模块
 */
@Controller
@RequestMapping("/sys/aclModule")
public class SysAclModuleController {

    @Autowired
    private SysModuleTreeService sysModuleTreeService;

    @Autowired
    private SysAclModuleService sysAclModuleService;

    @RequestMapping("/acl.page")
    public ModelAndView moduleManage(){
        return new ModelAndView("acl");
    }

    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData moduleTree(){
        List<SysAclModuleDto> sysAclModuleDtos = sysModuleTreeService.deptTree();
        return JsonData.success(sysAclModuleDtos);
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(SysAclModuleParam param){
        sysAclModuleService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData update(SysAclModuleParam param){
        sysAclModuleService.update(param);
        return JsonData.success("修改成功");
    }

    @RequestMapping("/delete.json")
    @ResponseBody
    public JsonData delete(Integer id){
        sysAclModuleService.delete(id);
        return JsonData.success("删除成功");
    }

}
