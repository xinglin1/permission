package com.xmcc.controller;

import com.xmcc.dto.SysDeptLevelDto;
import com.xmcc.param.DeptParam;
import com.xmcc.service.SysDeptService;
import com.xmcc.service.SysTreeService;
import com.xmcc.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


/**
 * @author 张兴林
 * @date 2019-03-21 17:02
 */
@Controller
@RequestMapping("/sys/dept")
public class SysDeptController {

    @Autowired
    private SysDeptService sysDeptService;

    @Autowired
    private SysTreeService sysTreeService;

    @RequestMapping("/dept.page")
    public ModelAndView userManage(){
        return new ModelAndView("dept");
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(DeptParam param){
        sysDeptService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData tree(){
        List<SysDeptLevelDto> sysDeptLevelDtoList = sysTreeService.deptTree();
        return JsonData.success(sysDeptLevelDtoList);
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData update(DeptParam param){
        sysDeptService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/delete.json")
    @ResponseBody
    public JsonData delete(Integer id){
        sysDeptService.delete(id);
        return JsonData.success();
    }

}
