package cn.bluebubbles.store.controller.backend;

import cn.bluebubbles.store.common.Const;
import cn.bluebubbles.store.common.ResponseCode;
import cn.bluebubbles.store.common.ServerResponse;
import cn.bluebubbles.store.pojo.User;
import cn.bluebubbles.store.service.IOrderService;
import cn.bluebubbles.store.service.IUserService;
import cn.bluebubbles.store.vo.OrderVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author yibo
 * @date 2019-01-13 19:25
 * @description 后台订单管理controller
 */
@RestController
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;

    /**
     * 后台获取所有订单列表
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/list")
    public ServerResponse<PageInfo> orderList(HttpSession session,
                                              @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "10")int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSucess()) {
            return iOrderService.manageList(pageNum, pageSize);
        }
        return ServerResponse.createByErrorMessage("无权限操作");
    }

    /**
     * 后台根据订单号获取订单详情
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("/detail")
    public ServerResponse<OrderVo> orderDetail(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSucess()) {
            return iOrderService.manageDetail(orderNo);
        }
        return ServerResponse.createByErrorMessage("无权限操作");
    }

    /**
     * 后台根据订单号搜索订单信息
     * @param session
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/search")
    public ServerResponse<PageInfo> orderSearch(HttpSession session, Long orderNo,
                                                @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                                @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSucess()) {
            return iOrderService.manageSearch(orderNo, pageNum, pageSize);
        }
        return ServerResponse.createByErrorMessage("无权限操作");
    }

    /**
     * 后台发货
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping("/send_goods")
    public ServerResponse<String> orderSendGoods(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSucess()) {
            return iOrderService.manageSendGoods(orderNo);
        }
        return ServerResponse.createByErrorMessage("无权限操作");
    }
}
