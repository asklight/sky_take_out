package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理相关接口
 */
@Api(tags = "菜品管理相关接口")
@RestController
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @ApiOperation(value = "新增菜品")
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO){
        dishService.saveWithFlavor(dishDTO);

        //新增菜品后，涉及到菜品列表的缓存失效，所以可以在这里进行相关的缓存清理操作，例如删除对应分类的菜品列表缓存。
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);

        return Result.success();
    }

    /**
     * 分页查询菜品信息
     * @param dishPageQueryDTO
     * @return
     */
    @ApiOperation(value = "分页查询菜品信息")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    @ApiOperation(value = "批量删除菜品")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品，ids：{}", ids);
        dishService.deleteBatch(ids);

        //批量删除菜品后，涉及到菜品列表的缓存失效，所以可以在这里进行相关的缓存清理操作，例如删除对应分类的菜品列表缓存。
//        ids.forEach(id -> {
//            //查询菜品信息，获取分类id
//            DishVO dish = dishService.getByIdWithFlavor(id);
//            String key = "dish_" + dish.getCategoryId();
//            redisTemplate.delete(key);
//        });
        //为了简化操作，可以直接清理所有菜品列表的缓存，或者根据实际情况选择性清理相关分类的菜品列表缓存。
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据id查询菜品信息
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id查询菜品信息")
    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){

        log.info("根据id查询菜品信息，id：{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品信息
     * @param dishDTO
     * @return
     */
    @ApiOperation(value = "修改菜品信息")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品信息，dishDTO：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        //修改菜品后，涉及到菜品列表的缓存失效，所以可以在这里进行相关的缓存清理操作，例如删除对应分类的菜品列表缓存。
        cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 启用或禁用菜品
     * @param status
     * @param id
     * @return
     */
    @ApiOperation(value = "启用或禁用菜品")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("启用或禁用菜品，status：{}，ids：{}", status, id);
        dishService.startOrStop(status, id);

        //启用或禁用菜品后，涉及到菜品列表的缓存失效，所以可以在这里进行相关的缓存清理操作，例如删除对应分类的菜品列表缓存。
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据分类id查询菜品信息
     * @param dishDTO
     * @return
     */
    @ApiOperation(value = "根据分类id查询菜品信息")
    @GetMapping("/list")
    public Result<List<Dish>> list(DishDTO dishDTO){
        log.info("根据分类id查询菜品信息，categoryId：{}", dishDTO.getCategoryId());
        List<Dish> dishList = dishService.list(dishDTO);
        return Result.success(dishList);
    }

    /**
     * 清理缓存的工具方法
     * @param pattern
     */
    private void cleanCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
