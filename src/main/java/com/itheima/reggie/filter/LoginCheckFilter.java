package com.itheima.reggie.filter;

/**
 * @author amass_
 * @date 2021/10/15
 */

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器,支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();

        //输出日志到控制台
        log.info("拦截到请求：{}", requestURI);

        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3.如果不需要处理,则直接放行
        if (check) {
            //控制台输出日志
            log.info("本次请求{}不需要处理", requestURI);
            //将请求放给下一个filter，如果没有filter那就是你请求的资源
            filterChain.doFilter(request, response);
            return;
        }

        //4.判断登录状态,如果已登录,则直接放行
        if (request.getSession().getAttribute("userInfo") != null) {
            //控制台输出日志
            log.info("用户已经登录,用户id为:{}",
                    request.getSession().getAttribute("userInfo"));

            //将线程id设置为登录者id,方便后期使用
            Long empId = (Long) request.getSession().getAttribute("userInfo");
            BaseContext.setCurrentId(empId);

            //将请求放给下一个filter，如果没有filter那就是你请求的资源
            filterChain.doFilter(request, response);
            return;
        }
        //4.2.判断登录状态,如果已登录,则直接放行
        if (request.getSession().getAttribute("user") != null) {
            //控制台输出日志
            log.info("用户已经登录,用户id为:{}",
                    request.getSession().getAttribute("user"));

            //将线程id设置为登录者id,方便后期使用
            Long empId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(empId);

            //将请求放给下一个filter，如果没有filter那就是你请求的资源
            filterChain.doFilter(request, response);
            return;
        }

        log.info("用户未登录");
        //5.如果未登录则返回未登录的结果,通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     *
     * @param urls
     * @param requestURI
     * @return
     */
    private boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}