package com.xmcc.utils;

import com.xmcc.model.SysUser;

import javax.servlet.http.HttpServletRequest;

public class RequestHolder {

    //绑定当前登录用户
    private static final ThreadLocal<SysUser> userHolder = new ThreadLocal<>();
    //绑定当前request对象
    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();


    public static void add(SysUser sysUser){
        userHolder.set(sysUser);
    }

    public static void add(HttpServletRequest request){
        requestHolder.set(request);
    }

    public static SysUser getUser(){
        return userHolder.get();
    }

    public static HttpServletRequest getRequest(){
        return requestHolder.get();
    }

    public static void remove(){
        userHolder.remove();
        requestHolder.remove();
    }

}
