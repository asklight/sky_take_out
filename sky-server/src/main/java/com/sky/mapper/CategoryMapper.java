package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {

    /**
     * 插入分类数据
     * @param category
     */
    @Insert("insert into category (type, name, sort, create_time, "
            + "status, update_time, create_user, update_user) "
            + "values (#{type}, #{name}, #{sort}, #{createTime}, "
            + "#{status}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void save(Category category);

    /**
     * 分页查询外卖分类信息
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     *
     * @param id
     */
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据id动态修改外卖分类信息
     * @param category
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);
}
