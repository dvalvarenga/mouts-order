package com.mouts.order.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    private final ObjectMapper mapper;

    public KafkaOrderConsumer(OrderService orderService, ObjectMapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "${kafka.topics.pending-orders}", groupId = "orders-group")
    public void consumePendingOrders(String message)  {

        log.info("Nova mensagem lida no tópico.");

        try {
            Order order = mapper.readValue(message, Order.class);
            orderService.validateOrder(order);
            log.info("Pedido finalizado com sucesso !");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro de desserialização ao processar mensagem do Kafka.", e);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao processar mensagem do Kafka.", ex);
        }
    }
}
