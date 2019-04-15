package com.xmcc.filter;

import com.xmcc.model.SysUser;
import com.xmcc.service.SysCoreService;
import com.xmcc.utils.ApplicationContextHelper;
import com.xmcc.utils.JsonData;
import com.xmcc.utils.JsonMapper;
import com.xmcc.utils.RequestHolder;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/sys/*")
public class PermissionInterceptFilter implements Filter {

    private final static String noAuthUrl = "/sys/user/noAuth.page";

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        //获取uri
        String uri = request.getRequestURI();
        System.out.println(uri);
        if (uri.contains("noAuth.jsp") || uri.contains("login") || uri.contains("signin")){
            chain.doFilter(req, resp);
            return;
        }
        SysUser user = RequestHolder.getUser();
        if (user == null){
            //无权限时
            noAuth(request,response);
            return;
        }
        SysCoreService sysCoreService = ApplicationContextHelper.popBean(SysCoreService.class);
        System.out.println(sysCoreService.hasAcl(uri));
        if (!sysCoreService.hasAcl(uri)){
            noAuth(request,response);
            return;
        }
        chain.doFilter(req, resp);
    }

    private void noAuth(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        if (uri.endsWith(".json")){
            JsonData jsonData = JsonData.fail("没有访问权限，如需要请充钱");
            response.setContentType("text/html;charset=utf-8");
            response.setHeader("content-type","application/json");
            try {
                response.getWriter().print(JsonMapper.obj2String(jsonData));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            response.setHeader("Content-Type", "text/html");
            try {
                response.getWriter().print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                        + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + "<head>\n" + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n"
                        + "<title>跳转中...</title>\n" + "</head>\n" + "<body>\n" + "跳转中，请稍候...\n" + "<script type=\"text/javascript\">//<![CDATA[\n"
                        + "window.location.href='" + noAuthUrl + "?ret='+encodeURIComponent(window.location.href);\n" + "//]]></script>\n" + "</body>\n" + "</html>\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
