package com.mouts.order.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mouts.order.record.OrderRecord;
import com.mouts.order.entity.Order;
import com.mouts.order.util.OrderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class KafkaValidatedOrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OrderUtil orderUtil;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.validated-orders}")
    private String validatedOrdersTopic;

    public KafkaValidatedOrderProducer(KafkaTemplate<String, String> kafkaTemplate, OrderUtil orderUtil, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderUtil = orderUtil;
        this.objectMapper = objectMapper;
    }

    public void sendValidatedOrder(Order order) {
        try {
            OrderRecord orderRecord = orderUtil.toOrderRecord(order);
            String message = objectMapper.writeValueAsString(orderRecord);
            kafkaTemplate.send(validatedOrdersTopic, message);
            log.info("Pedido validado enviado para a fila {} .", validatedOrdersTopic);
        } catch (JsonProcessingException e) {
            log.error("Erro ao enviar pedido para a fila {} : {}.", validatedOrdersTopic, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
