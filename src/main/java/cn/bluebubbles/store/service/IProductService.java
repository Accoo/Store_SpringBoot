package cn.bluebubbles.store.service;

import cn.bluebubbles.store.common.ServerResponse;
import cn.bluebubbles.store.pojo.Product;
import cn.bluebubbles.store.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;

/**
 * @author yibo
 * @date 2019-01-12 14:39
 * @description
 */
public interface IProductService {
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);

    ServerResponse<String> saveOrUpdateProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId,Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);
}
