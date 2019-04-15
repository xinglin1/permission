package com.xmcc.controller;

import com.xmcc.beans.PageQuery;
import com.xmcc.beans.SearchLog;
import com.xmcc.model.SysLog;
import com.xmcc.model.SysLogWithBLOBs;
import com.xmcc.service.SysLogService;
import com.xmcc.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author 张兴林
 * @date 2019-04-02 09:15
 */
@Controller
@RequestMapping("/sys/log")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    @RequestMapping("/log.page")
    public ModelAndView log(){
        return new ModelAndView("log");
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData page(SearchLog searchLog, PageQuery<SysLogWithBLOBs> pageBean){
        PageQuery<SysLogWithBLOBs> search = sysLogService.search(searchLog, pageBean);
        return JsonData.success(search);
    }

    @RequestMapping("/recover.json")
    @ResponseBody
    public JsonData recover(Integer id){
        sysLogService.restore(id);
        return JsonData.success();
    }

}
