package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {

    /**
     * 新增外卖分类
     * @param categoryDTO
     */
    public void save(CategoryDTO categoryDTO);

    /**
     * 分页查询外卖分类信息
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id删除外卖分类信息
     * @param id
     */
    void deleteById(Long id);

    /**
     * 根据id动态修改外卖分类信息
     * @param categoryDTO
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 启用或禁用外卖分类
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 查询外卖分类列表
     * @return
     */
    List<Category> list(Integer type);
}
