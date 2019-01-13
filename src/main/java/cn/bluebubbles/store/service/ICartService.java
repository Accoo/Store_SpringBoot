package cn.bluebubbles.store.service;

import cn.bluebubbles.store.common.ServerResponse;
import cn.bluebubbles.store.vo.CartVo;

/**
 * @author yibo
 * @date 2019-01-13 10:57
 * @description
 */
public interface ICartService {
    ServerResponse<CartVo> list (Integer userId);

    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count);

    ServerResponse<CartVo> deleteProduct(Integer userId,String productIds);

    ServerResponse<CartVo> selectOrUnSelect (Integer userId,Integer productId,Integer checked);

    ServerResponse<Integer> getCartProductCount(Integer userId);
}
