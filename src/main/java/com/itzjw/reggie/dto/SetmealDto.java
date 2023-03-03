package com.itzjw.reggie.dto;

import com.itzjw.reggie.entity.Setmeal;
import com.itzjw.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
