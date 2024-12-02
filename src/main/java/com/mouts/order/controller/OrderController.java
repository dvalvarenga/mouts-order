package com.mouts.order.controller;

import com.mouts.order.dto.OrderDTO;
import com.mouts.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/all")
    public List<OrderDTO> getAllOrders() {
        return orderService.getAllOrdersAsDTO();
    }
 }
