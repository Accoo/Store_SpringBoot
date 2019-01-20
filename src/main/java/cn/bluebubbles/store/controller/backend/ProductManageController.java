package cn.bluebubbles.store.controller.backend;

import cn.bluebubbles.store.common.ResponseCode;
import cn.bluebubbles.store.common.ServerResponse;
import cn.bluebubbles.store.pojo.Product;
import cn.bluebubbles.store.pojo.User;
import cn.bluebubbles.store.service.IFileService;
import cn.bluebubbles.store.service.IProductService;
import cn.bluebubbles.store.service.IUserService;
import cn.bluebubbles.store.util.CookieUtil;
import cn.bluebubbles.store.util.JsonUtil;
import cn.bluebubbles.store.util.PropertiesUtil;
import cn.bluebubbles.store.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yibo
 * @date 2019-01-12 16:34
 * @description 商品后台controller
 */
@RestController
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     * 更新或新增商品
     * @param request
     * @param product
     * @return
     */
    @RequestMapping("/save")
    public ServerResponse<String> productSave(HttpServletRequest request, Product product) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        String userJson = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJson, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSucess()) {
            return iProductService.saveOrUpdateProduct(product);
        }
        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
    }

    /**
     * 修改商品销售状态
     * @param request
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("/set_sale_status")
    public ServerResponse<String> setSaleStatus(HttpServletRequest request, Integer productId, Integer status) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        String userJson = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJson, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSucess()) {
            return iProductService.setSaleStatus(productId, status);
        }
        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
    }

    /**
     * 获取商品详情
     * @param request
     * @param productId
     * @return
     */
    @RequestMapping("/detail")
    public ServerResponse getDetail(HttpServletRequest request, Integer productId) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        String userJson = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJson, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSucess()) {
            return iProductService.manageProductDetail(productId);
        }
        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
    }

    /**
     * 获取商品列表
     * @param request
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/list")
    public ServerResponse getList(HttpServletRequest request,
                                  @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        String userJson = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJson, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSucess()) {
            return iProductService.getProductList(pageNum, pageSize);
        }
        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
    }

    /**
     * 根据商品名或商品ID搜索商品
     * @param request
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/search")
    public ServerResponse productSearch(HttpServletRequest request, String productName,Integer productId,
                                        @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize",defaultValue = "10") int pageSize) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        String userJson = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJson, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSucess()) {
            return iProductService.searchProduct(productName, productId,pageNum, pageSize);
        }
        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
    }

    /**
     * 上传图片
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("/upload")
    public ServerResponse upload(HttpServletRequest request,
                                 @RequestParam(value = "upload_file", required = false)MultipartFile file) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        String userJson = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJson, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSucess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            Map fileMap = new HashMap(2);
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);
        }
        return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
    }

    /**
     * 富文本上传
     * @param file
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/richtext_img_upload")
    public Map richtextImgUpload(@RequestParam(value = "upload_file",required = false) MultipartFile file,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        Map resultMap = new HashMap();
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            resultMap.put("success", false);
            resultMap.put("msg", "无权限操作,需要管理员权限");
            return resultMap;
        }
        String userJson = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJson, User.class);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "无权限操作,需要管理员权限");
            return resultMap;
        }
        if (iUserService.checkAdminRole(user).isSucess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            if (StringUtils.isBlank(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }
        resultMap.put("success", false);
        resultMap.put("msg", "无权限操作,需要管理员权限");
        return resultMap;
    }
}
