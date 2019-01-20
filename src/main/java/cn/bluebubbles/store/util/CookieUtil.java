package cn.bluebubbles.store.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yibo
 * @date 2019-01-20 09:45
 * @description
 */
@Slf4j
public class CookieUtil {

    /**
     * Cookie的域名,由于没有部署在线上环境,所以此字段暂时不用
     */
    // private final static String COOKIE_DOMAIN = ".bluebubbles.cn";

    /**
     * Cookie的名字为商店登录的token
     */
    private final static String COOKIE_NAME = "STORE_LOGIN_TOKEN";

    /**
     * 读取请求中的登录相关的cookie
     * @param request
     * @return
     */
    public static String readLoginToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                log.info("read cookieName:{}, cookieValue:{}", cookie.getName(), cookie.getValue());
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 向响应中添加登录cookie
     * @param response
     * @param token
     */
    public static void writeLoginToken(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        // cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        // cookie有效期,单位为秒,如果是-1则代表永久有效,如果不设置maxage,cookie就不会写入硬盘,而是写在内存,只在当前页面有效
        cookie.setMaxAge(60 * 60 * 24 * 30);
        log.info("write cookieName:{},cookieValue:{}", cookie.getName(), cookie.getValue());
        response.addCookie(cookie);
    }

    /**
     * 删除登录cookie
     * @param request
     * @param response
     */
    public static void deleteLoginToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                // cookie.setDomain(COOKIE_DOMAIN);
                cookie.setPath("/");
                // 设置为0表示删除该cookie
                cookie.setMaxAge(0);
                log.info("delete cookieName:{},cookieValue:{}", cookie.getName(), cookie.getValue());
                response.addCookie(cookie);
                return;
            }
        }
    }
}
