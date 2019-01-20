package cn.bluebubbles.store.controller.portal;

import cn.bluebubbles.store.common.Const;
import cn.bluebubbles.store.common.ResponseCode;
import cn.bluebubbles.store.common.ServerResponse;
import cn.bluebubbles.store.pojo.User;
import cn.bluebubbles.store.service.IUserService;
import cn.bluebubbles.store.util.CookieUtil;
import cn.bluebubbles.store.util.JsonUtil;
import cn.bluebubbles.store.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author      yibo
 * @date        2019-01-11 14:35
 * @description 前台用户controller
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登陆
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse response) {
        ServerResponse<User> result = iUserService.login(username, password);
        if (result.isSucess()) {
            CookieUtil.writeLoginToken(response, session.getId());
            // 将用户登录的session ID放到Redis中
            RedisPoolUtil.setex(session.getId(), JsonUtil.obj2String(result.getData()), Const.REDIS_SESSION_EXTIME);
        }
        return result;
    }

    /**
     * 用户登出
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ServerResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByError();
        }
        CookieUtil.deleteLoginToken(request, response);
        RedisPoolUtil.del(loginToken);
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    /**
     * 根据type校验用户名或邮箱
     * @param str  要校验的字符串
     * @param type 校验的类型（用户名或邮箱）
     * @return
     */
    @RequestMapping(value = "/check_valid", method = RequestMethod.POST)
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    /**
     * 获取当前登陆用户的信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/get_user_info", method = RequestMethod.POST)
    public ServerResponse<User> getUserInfo(HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆,无法获取当前用户信息");
        }
        String userJson = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJson, User.class);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登陆,无法获取当前用户信息");
    }

    /**
     * 获取密保问题
     * @param username
     * @return
     */
    @RequestMapping(value = "/forget_get_question", method = RequestMethod.POST)
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    /**
     * 校验用户密保答案
     * @param username 用户名
     * @param question 密保问题
     * @param answer   密保答案
     * @return 返回一个token,重置密码请求需要携带此token
     */
    @RequestMapping(value = "/forget_check_answer", method = RequestMethod.POST)
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 忘记密码中的重置密码
     * @param username    用户名
     * @param newPassword 新密码
     * @param forgetToken 校验密保问题后得到的token,防止横向越权,token只有在正确回答密保问题后才能拿到
     * @return
     */
    @RequestMapping(value = "/forget_reset_password", method = RequestMethod.POST)
    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken) {
        return iUserService.forgetResetPassword(username, newPassword, forgetToken);
    }

    /**
     * 重置密码
     * @param request
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @RequestMapping(value = "/reset_password", method = RequestMethod.POST)
    public ServerResponse<String> resetPassword(HttpServletRequest request, String oldPassword, String newPassword) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        String userJson = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJson, User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return iUserService.resetPassword(oldPassword, newPassword, user);
    }

    /**
     * 更新个人信息
     * @param request
     * @param user
     * @return
     */
    @RequestMapping(value = "/update_information", method = RequestMethod.POST)
    public ServerResponse<User> updateInfo(HttpServletRequest request, User user) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        String userJson = RedisPoolUtil.get(loginToken);
        User currentUser = JsonUtil.string2Obj(userJson, User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInfo(user);
        if (response.isSucess()) {
            response.getData().setUsername(currentUser.getUsername());
            RedisPoolUtil.setex(loginToken, JsonUtil.obj2String(response.getData()), Const.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    /**
     * 获取当前登陆的用户信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/get_information", method = RequestMethod.POST)
    public ServerResponse<User> getInfo(HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "未登陆,需要强制登陆status=10");
        }
        String userJson = RedisPoolUtil.get(loginToken);
        User currentUser = JsonUtil.string2Obj(userJson, User.class);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "未登陆,需要强制登陆status=10");
        }
        return iUserService.getInfo(currentUser.getId());
    }
}
