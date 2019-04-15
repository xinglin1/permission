package com.xmcc.filter;

import com.xmcc.model.SysUser;
import com.xmcc.utils.RequestHolder;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class LoginFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String uri = request.getRequestURI();
        if (uri.contains("login") || uri.contains("signin")){
            chain.doFilter(request,response);
            return;
        } else {
            SysUser sysUser = (SysUser) request.getSession().getAttribute("user");
            if (sysUser == null){
                response.sendRedirect("/signin.jsp");
                return;
            } else {
                //将user和request绑定在ThreadLocal中
                RequestHolder.add(sysUser);
                RequestHolder.add(request);
                chain.doFilter(request, response);
            }
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
