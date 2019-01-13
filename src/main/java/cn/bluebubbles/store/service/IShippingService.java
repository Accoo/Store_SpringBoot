package cn.bluebubbles.store.service;

import cn.bluebubbles.store.common.ServerResponse;
import cn.bluebubbles.store.pojo.Address;
import com.github.pagehelper.PageInfo;

/**
 * @author yibo
 * @date 2019-01-13 13:46
 * @description
 */
public interface IShippingService {
    ServerResponse add(Integer userId, Address address);

    ServerResponse<String> del(Integer userId,Integer addressId);

    ServerResponse<String> update(Integer userId, Address address);

    ServerResponse<Address> select(Integer userId, Integer addressId);

    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
