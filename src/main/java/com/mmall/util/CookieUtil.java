package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 流程：
 * 登陆的时候将COOKIE_NAME,和sessionId生成cookie进行种入
 * 将sessionId和User的信息存入到redis当中
 * 当均衡负载的时候，只需要登陆一次，调用其他接口的时候
 * 从request当中拿到对应的COOKIE_NAME所对应的value值
 * 然后去redis中拿出存入的User信息
 */
@Slf4j
public class CookieUtil {
    /**
     * cookie中的Domain的选项为，如果声明了一个domain的作用域
     * 比如下面的domain的作用域为.happy.com ，说明只有在其子域名才能拿到这个cookie
     */
    private final static String COOKIE_DOMAIN = ".happy.com";
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

    /**
     *
     * x:domain=".happy.com"
     * a:A.happy.com                        cookie: domain=A.happy.com :path="/"
     * b:B.happy.com                        cookie: domain=B.happy.com :path="/"
     * c:A.happy.com/test/cc                cookie: domain=A.happy.com :path="/test/cc"
     * d:A.happy.com/test/dd                cookie: domain=A.happy.com :path="/test/dd"
     * e:A.happy.com/test                   cookie: domain=A.happy.com :path="/test"
     * a,b,c,d都能拿到x的cookie
     * a,b不能相互拿到对方的cookie
     * c能够拿到a的cookie
     * c，d不能相互拿到cookie
     * c,d能够拿到e的cookie
     *
     *
     * @param httpServletResponse
     * @param value
     */


//写入cookie
    public static void writeLoginToken(HttpServletResponse httpServletResponse, String value) {
        Cookie cookie = new Cookie(COOKIE_NAME, value);
        cookie.setPath("/");//代表设置在根目录下
        //防止脚本和第三方获取cookie
        cookie.setHttpOnly(true);

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
