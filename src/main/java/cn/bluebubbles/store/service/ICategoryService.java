package cn.bluebubbles.store.service;

import cn.bluebubbles.store.common.ServerResponse;
import cn.bluebubbles.store.pojo.Category;

import java.util.List;

/**
 * @author yibo
 * @date 2019-01-12 13:14
 * @description
 */
public interface ICategoryService {
    ServerResponse<String> addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategoryName(Integer categoryId,String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
