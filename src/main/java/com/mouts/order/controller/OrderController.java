package com.mouts.order.controller;

import com.mouts.order.record.OrderRecord;
import com.mouts.order.service.OrderCacheService;
import com.mouts.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderCacheService orderCacheService;

    public OrderController(OrderService orderService, OrderCacheService orderCacheService) {
        this.orderService = orderService;
        this.orderCacheService = orderCacheService;
    }

    @GetMapping("/all")
    public List<OrderRecord> getAllOrders() {
        log.info("tempo");
        return orderCacheService.getAllOrders();
    }


    // endpoint somente para fins de apresentação.
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendOrderToKafka(@RequestBody String message) {
        orderService.newOrder(message);
    }

 }
