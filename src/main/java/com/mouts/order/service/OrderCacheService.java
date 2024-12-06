package com.mouts.order.service;

import com.mouts.order.record.OrderRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class OrderCacheService {

    @Autowired
    private OrderService orderService;

    @Cacheable(value = "orders", key="'allOrders'" ,cacheManager = "cacheManager")
    public List<OrderRecord> getAllOrders() {
        log.info("Salvando no cache pela primeira vez...");
        return orderService.getAllOrdersAsRecords();
    }
}
