package com.mouts.order.controller;

import com.mouts.order.exception.DuplicateOrderException;
import com.mouts.order.record.OrderRecord;
import com.mouts.order.service.OrderCacheService;
import com.mouts.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
        long startTime = System.currentTimeMillis();
        List<OrderRecord> orders = orderCacheService.getAllOrders();
        long endTime = System.currentTimeMillis();
        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        return orders;
    }


    @PostMapping("/send")
    public ResponseEntity<String> sendOrderToKafka(@RequestBody String message) {
        orderService.newOrder(message);
        return ResponseEntity.ok("Pedido enviado com sucesso !");
    }

 }
