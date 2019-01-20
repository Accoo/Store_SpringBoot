package cn.bluebubbles.store.service.impl;

import cn.bluebubbles.store.common.Const;
import cn.bluebubbles.store.common.ServerResponse;
import cn.bluebubbles.store.common.TokenCache;
import cn.bluebubbles.store.dao.UserMapper;
import cn.bluebubbles.store.pojo.User;
import cn.bluebubbles.store.service.IUserService;
import cn.bluebubbles.store.util.MD5Util;
import cn.bluebubbles.store.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author yibo
 * @date 2019-01-11 14:57
 * @description
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登陆
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        // 将密码用MD5加密
        String md5Password = MD5Util.MD5Encode(password);

        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户名或密码密码错误");
        }
        user.setPassword("");
        return ServerResponse.createBySuccess("登陆成功", user);
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSucess()) {
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSucess()) {
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5Encode(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 根据type校验用户名或邮箱是否已经存在
     * @param str
     * @param type
     * @return
     */
    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNoneBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /**
     * 忘记密码中的密保问题
     * @param username
     * @return
     */
    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSucess()) {
            // 用户不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNoneBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空");
    }

    /**
     * 校验密保答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            // 说明这个问题及答案是这个用户的，并且是正确的
            String forgetToken = UUID.randomUUID().toString();
            RedisPoolUtil.setex(TokenCache.TOKEN_PREFIX + username, forgetToken, 60 * 30);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    /**
     * 忘记密码中的重置密码
     * @param username
     * @param newPassword
     * @param forgenToken
     * @return
     */
    @Override
    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgenToken) {
        if (StringUtils.isBlank(forgenToken)) {
            return ServerResponse.createByErrorMessage("参数错误,需要传递token");
        }
        ServerResponse<String> validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSucess()) {
            // 用户不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String token = RedisPoolUtil.get(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token无效或过期");
        }
        if (StringUtils.equals(forgenToken, token)) {
            String md5Password = MD5Util.MD5Encode(newPassword);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("重置密码成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token");
        }
        return ServerResponse.createByErrorMessage("重置密码失败");
    }

    /**
     * 重置密码
     * @param oldPassword
     * @param newPassword
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> resetPassword(String oldPassword, String newPassword, User user) {
        // 防止横向越权,要校验这个用户的旧密码一定要指定这个用户
        int resultCount = userMapper.checkPassword(MD5Util.MD5Encode(oldPassword), user.getId());
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5Encode(newPassword));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    /**
     * 更新个人信息,ID和username不能被更新
     * @param user
     * @return
     */
    @Override
    public ServerResponse<User> updateInfo(User user) {
        // username和ID不能被更新
        // 校验email,校验新的email是否和别的用户的email相同
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 0) {
            return ServerResponse.createByErrorMessage("email已经存在,请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    /**
     * 获取个人信息,并在返回前将用户的密码置空
     * @param userId
     * @return
     */
    @Override
    public ServerResponse<User> getInfo(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword("");
        return ServerResponse.createBySuccess(user);
    }

    /**
     * 校验是否是管理员（此方法供后台使用）
     * @param user
     * @return
     */
    @Override
    public ServerResponse checkAdminRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
