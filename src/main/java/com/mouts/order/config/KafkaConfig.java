package com.mouts.order.config;

import com.mouts.order.exception.DuplicateOrderException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
@EnableKafka
public class KafkaConfig {

    private static final long BACKOFF_INTERVAL = 1000L;
    private static final int MAX_ATTEMPTS = 3;

    @Bean
    public DefaultErrorHandler errorHandler(
            KafkaTemplate<String, String> kafkaTemplate) {

        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, exception) -> {
                            log.error("Mensagem para dead letter - Tópico: {}, Mensagem: {}, Erro: {}",
                                    record.topic() + ".DLQ",
                                    record.value(),
                                    exception.getMessage()
                            );
                            return new TopicPartition(record.topic() + ".DLQ", record.partition());
                        }
                );

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                deadLetterPublishingRecoverer,
                new FixedBackOff(BACKOFF_INTERVAL, MAX_ATTEMPTS)
        ) {
            @Override
            public boolean handleOne(Exception thrownException,
                                     org.apache.kafka.clients.consumer.ConsumerRecord<?, ?> record,
                                     org.apache.kafka.clients.consumer.Consumer<?, ?> consumer,
                                     org.springframework.kafka.listener.MessageListenerContainer container) {
                Throwable rootCause = findRootCause(thrownException);

                if (rootCause instanceof DuplicateOrderException) {
                    log.warn(rootCause.getMessage());
                }
                return super.handleOne(thrownException, record, consumer, container);
            }
        };

        errorHandler.addNotRetryableExceptions(DuplicateOrderException.class);

        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory,
            DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

    private Throwable findRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause != cause.getCause()) {
            cause = cause.getCause();
        }
        return cause;
    }
}