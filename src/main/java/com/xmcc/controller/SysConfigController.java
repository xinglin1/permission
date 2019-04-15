package com.xmcc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author 张兴林
 * @date 2019-04-02 09:18
 */
@Controller
@RequestMapping("/config")
public class SysConfigController {

    @RequestMapping("/config.page")
    public ModelAndView config(){
        return new ModelAndView("config");
    }

}
