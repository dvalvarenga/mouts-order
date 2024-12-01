package com.mouts.order.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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
            System.out.println("Mensagem enviada para a fila de retry: " + retryOrdersTopic);
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem para a fila de retry: " + e.getMessage());
        }
    }
}
