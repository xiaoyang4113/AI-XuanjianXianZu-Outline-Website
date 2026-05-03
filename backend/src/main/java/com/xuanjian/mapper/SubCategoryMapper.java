package com.xuanjian.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuanjian.entity.SubCategory;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SubCategoryMapper extends BaseMapper<SubCategory> {

    @Select("SELECT * FROM sub_category WHERE category_id = #{categoryId} ORDER BY sort_order")
    List<SubCategory> selectByCategoryId(Long categoryId);
}