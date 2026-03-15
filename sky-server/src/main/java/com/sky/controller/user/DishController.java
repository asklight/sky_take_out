package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {

        String key = "dish_" + categoryId;

        //使用redis思路:
        //1.先从redis中查询数据
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);

        //2.如果redis中有数据,直接返回
        if (list != null && list.size() > 0) {
            return Result.success(list);
        }

        //3.如果redis中没有数据,查询数据库,将数据存入redis,然后返回

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        //查询起售中的菜品
        dish.setStatus(StatusConstant.ENABLE);

        list = dishService.listWithFlavor(dish);
        //将数据存入redis,设置过期时间为1小时
        redisTemplate.opsForValue().set(key, list, 1, TimeUnit.HOURS);

        return Result.success(list);
    }

}
