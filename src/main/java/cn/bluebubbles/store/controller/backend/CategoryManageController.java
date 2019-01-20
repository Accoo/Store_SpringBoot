package cn.bluebubbles.store.controller.backend;

import cn.bluebubbles.store.common.ResponseCode;
import cn.bluebubbles.store.common.ServerResponse;
import cn.bluebubbles.store.pojo.User;
import cn.bluebubbles.store.service.ICategoryService;
import cn.bluebubbles.store.service.IUserService;
import cn.bluebubbles.store.util.CookieUtil;
import cn.bluebubbles.store.util.JsonUtil;
import cn.bluebubbles.store.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yibo
 * @date 2019-01-12 13:13
 * @description
 */
@RestController
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加品类
     * @param request
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping("/add_category")
    public ServerResponse<String> addCategory(HttpServletRequest request, String categoryName,
                                              @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆,请先登陆");
        }
        String userJson = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJson, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆,请先登陆");
        }
        // 校验是否为管理员
        if (iUserService.checkAdminRole(user).isSucess()) {
            return iCategoryService.addCategory(categoryName, parentId);
        }
        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
    }

    /**
     * 设置品类的名字
     * @param request
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping("/set_category_name")
    public ServerResponse<String> setCategoryName(HttpServletRequest request, Integer categoryId, String categoryName) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆,请先登陆");
        }
        String userJson = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJson, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆,请先登陆");
        }
        // 校验是否是管理员
        if (iUserService.checkAdminRole(user).isSucess()) {
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        }
        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
    }

    /**
     * 获取子分类列表,不递归
     * @param request
     * @param categoryId
     * @return
     */
    @RequestMapping("/get_category")
    public ServerResponse getChildrenParallelCategory(HttpServletRequest request,
                                                     @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆,请先登陆");
        }
        String userJson = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJson, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆,请先登陆");
        }
        if (iUserService.checkAdminRole(user).isSucess()) {
            // 查询子节点的category信息,不递归,保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }
        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
    }

    /**
     * 获取子分类列表,递归
     * @param request
     * @param categoryId
     * @return
     */
    @RequestMapping("/get_deep_category")
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpServletRequest request,
                                                             @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆,请先登陆");
        }
        String userJson = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJson, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登陆,请先登陆");
        }
        if (iUserService.checkAdminRole(user).isSucess()) {
            // 查询子节点的category信息,不递归,保持平级
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }
        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
    }
}
