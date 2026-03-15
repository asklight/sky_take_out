package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "套餐管理相关接口")
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @ApiOperation("新增套餐")
    @PostMapping
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 分页查询套餐信息
     * @param setmealPageQueryDTO
     * @return
     */
    @ApiOperation("分页查询套餐信息")
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启售/停售套餐
     * @param status
     * @param id
     * @return
     */
    @ApiOperation(value = "启售/停售套餐")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable("status") Integer status, @RequestParam("id") Long id){
        setmealService.statOrStop(status, id);
        return Result.success();
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @ApiOperation(value = "删除套餐")
    @DeleteMapping
    public Result delete(@RequestParam("ids") List<Long> ids){
        ids.forEach(id -> {
            log.info("删除套餐，id：{}", id);
            setmealService.delete(id);
        });
        return Result.success();
    }

    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id查询套餐信息")
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id){
        log.info("根据id查询套餐信息，id：{}", id);
        SetmealVO setmealVO = setmealService.getByIdWithDish(id);
        return Result.success(setmealVO);
    }

     /**
     * 修改套餐信息
     * @param setmealDTO
     * @return
     */
     @ApiOperation(value = "修改套餐信息")
     @PutMapping
     public Result update(@RequestBody SetmealDTO setmealDTO){
         log.info("修改套餐信息，setmealDTO：{}", setmealDTO);
         setmealService.update(setmealDTO);
         return Result.success();
     }
}
