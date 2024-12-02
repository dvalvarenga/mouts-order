package com.mouts.order.messaging;

import com.mouts.order.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaOrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topics.pending-orders}")
    private String pendingOrdersTopic;

    public KafkaOrderProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToPendingOrders(String message) {
        try {
            kafkaTemplate.send(pendingOrdersTopic, message);
            log.info("Mensagem enviada para a fila de validação e processamento: {}", pendingOrdersTopic);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem para a fila de validação e processamento {} : {}", pendingOrdersTopic, e.getMessage());
        }
    }
}
