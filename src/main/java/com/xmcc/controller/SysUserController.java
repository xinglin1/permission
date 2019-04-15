package com.xmcc.controller;

import com.xmcc.beans.PageQuery;
import com.xmcc.model.SysUser;
import com.xmcc.param.SysUserParam;
import com.xmcc.param.UserParam;
import com.xmcc.service.SysUserService;
import com.xmcc.utils.JsonData;
import com.xmcc.utils.RequestHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 张兴林
 * @date 2019-03-21 17:16
 */
@Controller
@RequestMapping("/sys/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @RequestMapping("/login.page")
    public String login(UserParam param, HttpServletRequest request){
        SysUser user = sysUserService.login(param);
        if (user == null){
            String msg = "用户名或者密码错误";
            request.getSession().setAttribute("error",msg);
            return "redirect:/signin.jsp";
        } else if (user.getStatus() != 1){
            String msg = "用户处于非正常状态";
            request.getSession().setAttribute("error",msg);
            return "redirect:/signin.jsp";
        } else {
            request.getSession().setAttribute("user",user);
            request.getSession().setAttribute("error","");
            return "admin";
        }
    }

    @RequestMapping("/loginout.page")
    public ModelAndView loginOut(HttpServletRequest request){
        RequestHolder.getRequest().getSession().setAttribute("user",null);
        RequestHolder.remove();
        return new ModelAndView("signin");
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(SysUserParam param){
        sysUserService.addUser(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData update(SysUserParam param){
        sysUserService.update(param);
        return JsonData.success(true);
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData page(int deptId, PageQuery<SysUser> pageQuery){
        PageQuery<SysUser> sysUserPageQuery = sysUserService.pageUser(deptId, pageQuery);
        return JsonData.success(sysUserPageQuery);
    }

    @RequestMapping("/page.do")
    @ResponseBody
    public JsonData pageDo(){
        return JsonData.success("page");
    }

    @RequestMapping("/noAuth.page")
    public ModelAndView noAuth(){
        return new ModelAndView("noAuth");
    }

}
