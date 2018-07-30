package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN=".raymond23.com";
    private final static String COOKIE_NAME="mmall_login_token";

    public static String readLoginCookie(HttpServletRequest request){
        Cookie[] cks=request.getCookies();
        if(cks!=null){
            for(Cookie ck:cks){
                log.info("read cookie_name:{},cookie_value:{}",ck.getName(),ck.getValue());
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    return ck.getValue();
                }
            }
        }
        return null;
    }

    public static void wtriteLoginToken(HttpServletResponse response,String token){
        Cookie ck=new Cookie(COOKIE_NAME,token);
        ck.setDomain(COOKIE_DOMAIN);
        ck.setPath("/");//设置为根目录
        ck.setMaxAge(60*60*24*365);//如果设置为-1，意味着有效期是永久，如果不设置，则cookie不会写入硬盘
        log.info("write cookie_name:{},cookie_value:{}",ck.getName(),ck.getValue());
        response.addCookie(ck);
    }

    public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cks=request.getCookies();
        if(cks!=null){
            for(Cookie ck:cks){
                if(StringUtils.equals(ck.getName(),COOKIE_NAME)){
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    ck.setMaxAge(0);//设置为0时，表示删除cookie
                    log.info("del cookie_name:{},cookie_value:{}",ck.getName(),ck.getValue());
                    response.addCookie(ck);
                    return;
                }
            }
        }
    }

}
