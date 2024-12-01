package com.mouts.order.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mouts.order.entity.Order;
import com.mouts.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaOrderConsumer {

    private final OrderService orderService;
    private final OrderRetryProducer orderRetryProducer;

    public KafkaOrderConsumer(OrderService orderService, OrderRetryProducer orderRetryProducer) {
        this.orderService = orderService;
        this.orderRetryProducer = orderRetryProducer;
    }

    @KafkaListener(topics = "${kafka.topics.pending-orders}", groupId = "orders-group")
    public void consumePendingOrders(String message) {
        log.info("Mensagem recebida: {}", message);
        try {
            ObjectMapper mapper = new ObjectMapper();
            Order order = mapper.readValue(message, Order.class);
            orderService.validateOrder(order);
        } catch (Exception e) {
            System.err.println("Erro ao processar a mensagem: " + e.getMessage());
            orderRetryProducer.sendToRetryQueue(message);
        }
    }
}
