package com.mmall.controller.common;

import com.mmall.common.Const;
import com.mmall.common.RedisPool;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {


    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest=(HttpServletRequest) servletRequest;

        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isNotEmpty(loginToken)){

            String userJsonStr= RedisPoolUtil.get(loginToken);
            User user= JsonUtil.string2Obj(userJsonStr,User.class);
             if(user!=null)
                 RedisPoolUtil.setEx(loginToken, Const.RedisCacheExtime.REDIS_SEESION_EXTIME);
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {


    }
}