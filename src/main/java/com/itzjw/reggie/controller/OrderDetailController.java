package com.itzjw.reggie.controller;

import com.itzjw.reggie.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/orderDetail")
@RestController
@Slf4j
public class OrderDetailController {

    private OrderDetailService orderDetailService;

}
