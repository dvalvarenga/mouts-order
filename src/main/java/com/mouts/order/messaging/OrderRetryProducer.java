package com.mouts.order.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderRetryProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topics.retry-orders}")
    private String retryOrdersTopic;

    public OrderRetryProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToRetryQueue(String message) {
        try {
            kafkaTemplate.send(retryOrdersTopic, message);
            log.info("Mensagem enviada para a fila de reprocessamento: {}", retryOrdersTopic);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem para a fila de reprocessamento {} : {}", retryOrdersTopic, e.getMessage());
        }
    }
}
