package cn.bluebubbles.store.filter;

import cn.bluebubbles.store.common.Const;
import cn.bluebubbles.store.pojo.User;
import cn.bluebubbles.store.util.CookieUtil;
import cn.bluebubbles.store.util.JsonUtil;
import cn.bluebubbles.store.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author yibo
 * @date 2019-01-20 11:38
 * @description 过滤所有请求,重置Redis中session的有效期
 */
@Order(1)
@WebFilter(filterName = "sessionExpireFilter", urlPatterns = "/*")
public class SessionExpireFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotEmpty(loginToken)) {
            String userJson = RedisPoolUtil.get(loginToken);
            User user = JsonUtil.string2Obj(userJson, User.class);
            if (user != null) {
                RedisPoolUtil.expire(loginToken, Const.REDIS_SESSION_EXTIME);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
