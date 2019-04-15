package com.xmcc.controller;

import com.xmcc.beans.PageQuery;
import com.xmcc.model.SysAcl;
import com.xmcc.model.SysUser;
import com.xmcc.param.SysAclParam;
import com.xmcc.param.SysUserParam;
import com.xmcc.service.SysAclService;
import com.xmcc.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 张兴林
 * @date 2019-03-26 16:44
 */
@Controller
@RequestMapping("/sys/acl")
public class SysAclController {

    @Autowired
    private SysAclService sysAclService;

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData page(Integer aclModuleId, PageQuery<SysAcl> page){
        PageQuery<SysAcl> sysAclPageQuery = sysAclService.pageAcl(aclModuleId, page);
        return JsonData.success(sysAclPageQuery);
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(SysAclParam param){
        sysAclService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData update(SysAclParam param){
        sysAclService.update(param);
        return JsonData.success(true);
    }

    @RequestMapping("/delete.json")
    @ResponseBody
    public JsonData delete(Integer id){
        sysAclService.delete(id);
        return JsonData.success(true);
    }

}
