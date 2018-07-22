package com.mmall.controller.common;

import com.mmall.common.Const;
import com.mmall.common.RedisPool;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SessionExpireFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest=(HttpServletRequest) request;
        String loginToken=CookieUtil.readLoginCookie(httpServletRequest);
        if(StringUtils.isNotEmpty(loginToken)){
            String userJsonStr=RedisShardedPoolUtil.get(loginToken);
            User user=JsonUtil.string2Obj(userJsonStr,User.class);
            if(user!=null){
                RedisShardedPoolUtil.expire(loginToken,Const.RedisCacheExTime.REDIS_SESSION_EXTIME);

            }
        }
        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
