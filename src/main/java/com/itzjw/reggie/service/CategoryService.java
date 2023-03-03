package com.itzjw.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itzjw.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long ids);
}
