package com.xuanjian.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuanjian.entity.Category;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface CategoryMapper extends BaseMapper<Category> {

    @Select("SELECT c.*, " +
            "(SELECT COUNT(*) FROM entity_item ei WHERE ei.category_id = c.id) AS item_count " +
            "FROM category c WHERE c.project_id = #{projectId} ORDER BY c.sort_order")
    List<Map<String, Object>> selectCategoriesWithCount(Long projectId);
}