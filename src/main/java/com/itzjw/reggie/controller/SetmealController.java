package com.itzjw.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzjw.reggie.common.R;
import com.itzjw.reggie.dto.SetmealDto;
import com.itzjw.reggie.entity.Category;
import com.itzjw.reggie.entity.Dish;
import com.itzjw.reggie.entity.Setmeal;
import com.itzjw.reggie.entity.SetmealDish;
import com.itzjw.reggie.service.CategoryService;
import com.itzjw.reggie.service.SetmealDishService;
import com.itzjw.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */

    @CacheEvict(value = "setmealCache", allEntries = true)
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息为：{}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item, setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category.getName() != null){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @CacheEvict(value = "setmealCache", allEntries = true)
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}", ids);
        setmealService.removeWithDish(ids);
        return null;
    }


    /**
     * 根据条件查询对应的套餐信息
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        log.info("Setmeal:{}", setmeal.toString());
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus, setmeal.getStatus());
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
//        setmealList.stream().map((item)->{
//            SetmealDto setmealDto = new SetmealDto();
//            BeanUtils.copyProperties(item, setmealDto);
//            Long setmealId = item.getId();
//            LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//            lambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealId);
//            List<SetmealDish> setmealDishes = setmealDishService.list(lambdaQueryWrapper);
//            setmealDto.setSetmealDishes(setmealDishes);
//            return setmealDto;
//        }).collect(Collectors.toList());
        return R.success(setmealList);
    }

}
