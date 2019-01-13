package cn.bluebubbles.store.service.impl;

import cn.bluebubbles.store.common.ServerResponse;
import cn.bluebubbles.store.dao.AddressMapper;
import cn.bluebubbles.store.pojo.Address;
import cn.bluebubbles.store.service.IShippingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yibo
 * @date 2019-01-13 13:47
 * @description
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 增加收获地址
     * @param userId
     * @param address
     * @return
     */
    @Override
    public ServerResponse add(Integer userId, Address address) {
        address.setUserId(userId);
        int rowCount = addressMapper.insert(address);
        if (rowCount > 0) {
            Map result = new HashMap();
            result.put("shippingId", address.getId());
            return ServerResponse.createBySuccess("新建地址成功", result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    /**
     * 删除收货地址
     * @param userId
     * @param addressId
     * @return
     */
    @Override
    public ServerResponse<String> del(Integer userId, Integer addressId) {
        int resultCount = addressMapper.deleteByAddressIdUserId(userId, addressId);
        if (resultCount > 0) {
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    /**
     * 更新收货地址
     * @param userId
     * @param address
     * @return
     */
    @Override
    public ServerResponse<String> update(Integer userId, Address address) {
        address.setUserId(userId);
        int rowCount = addressMapper.updateByAddress(address);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    /**
     * 根据收货地址ID选择查询收获地址
     * @param userId
     * @param addressId
     * @return
     */
    @Override
    public ServerResponse<Address> select(Integer userId, Integer addressId) {
        Address address = addressMapper.selectByAddressIdUserId(userId, addressId);
        if (address == null) {
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        return ServerResponse.createBySuccess("查询地址成功", address);
    }

    /**
     * 获取所有到收货地址
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Address> addressList = addressMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(addressList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
