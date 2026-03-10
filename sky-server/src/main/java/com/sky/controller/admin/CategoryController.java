package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "分类管理相关接口")
@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增外卖分类
     * @param categoryDTO
     * @return
     */
    @ApiOperation(value = "新增外卖分类")
    @PostMapping
    public Result save(@RequestBody CategoryDTO categoryDTO){
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 分页查询外卖分类信息
     * @return
     */
    @ApiOperation("分页查询外卖分类信息")
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id删除外卖分类信息
     * @param id
     * @return
     */
    @ApiOperation("根据id删除外卖分类信息")
    @DeleteMapping
    public Result deleteById(Long id){
        categoryService.deleteById(id);
        return Result.success();
    }

        /**
        * 根据id动态修改外卖分类信息
        * @param categoryDTO
        * @return
        */
    @ApiOperation("根据id动态修改外卖分类信息")
    @PutMapping
    public Result update(@RequestBody CategoryDTO categoryDTO){
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 启用或禁用外卖分类
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("启用或禁用外卖分类")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id){
        categoryService.startOrStop(status, id);
        return Result.success();
    }

}
