package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN = ".happymmall.com";
    private final static String COOKIE_NAME = "mmall_login_token";

//读取cookie
    public static String readLoginToken(HttpServletRequest httpServletRequest) {

        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie ck : cookies) {
                log.info("read CookieName:{},cookieValue:{} ", ck.getName(), ck.getValue());
                /**
                 * StringUtils.equals中加入了null的判断，直接用
                 */
                if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
                    log.info("return cookieName:{},cookieValue:{}", ck.getName(), ck.getValue());
                    return ck.getValue();
                }
            }
        }

        return null;
    }

//写入cookie
    public static void writeLoginToken(HttpServletResponse httpServletResponse, String value) {
        Cookie cookie = new Cookie(COOKIE_NAME, value);
        cookie.setPath("/");//代表设置在根目录下

        //单位为秒，如果这个maxAge不进行设置的话，cookie就不会写入硬盘，而是写入内存，只在当前页面有效
        //-1代表的永久
        cookie.setMaxAge(60 * 60 * 24 * 365);
        log.info("write CookieName:{},cookieValue:{}", cookie.getName(), cookie.getValue());
        httpServletResponse.addCookie(cookie);


    }
//删除对应的cookie
    public static void delLoginToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie ck : cookies) {

                if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    ck.setMaxAge(0);
                    log.info("del cookieName:{} cookieValue:{}", ck.getName(), ck.getValue());
                    httpServletResponse.addCookie(ck);
                      return;//当命中的时候，直接结束方法的进行，进行返回
                }
            }
        }
    }


}
