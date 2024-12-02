package com.mouts.order.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mouts.order.dto.OrderDTO;
import com.mouts.order.entity.Order;
import com.mouts.order.enums.OrderStatus;
import com.mouts.order.util.OrderUtil;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KafkaProcessedOrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OrderUtil orderUtil;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.processed-orders}")
    private String processedOrdersTopic;

    public KafkaProcessedOrderProducer(KafkaTemplate<String, String> kafkaTemplate,OrderUtil orderUtil, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderUtil = orderUtil;
        this.objectMapper = objectMapper;
    }

    public void sendProcessedOrder(Order order) {
        try {
            OrderDTO orderDTO =  orderUtil.toOrderDTO(order);
            orderDTO.setStatus(String.valueOf(OrderStatus.PROCESSED));
            String message = objectMapper.writeValueAsString(orderDTO);
            kafkaTemplate.send(processedOrdersTopic, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
