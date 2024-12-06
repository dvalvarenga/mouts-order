package com.mouts.order.service;

import com.mouts.order.entity.Order;
import com.mouts.order.enums.OrderEvent;
import com.mouts.order.enums.OrderStatus;
import com.mouts.order.messaging.KafkaProcessedOrderProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderStateMachineService {

    private final StateMachineFactory<OrderStatus, OrderEvent> stateMachineFactory;
    private final KafkaProcessedOrderProducer kafkaProcessedOrderProducer;

    @Autowired
    public OrderStateMachineService(StateMachineFactory<OrderStatus, OrderEvent> stateMachineFactory, KafkaProcessedOrderProducer kafkaProcessedOrderProducer) {
        this.stateMachineFactory = stateMachineFactory;
        this.kafkaProcessedOrderProducer = kafkaProcessedOrderProducer;
    }

    public void changeOrderStatus(OrderStatus currentStatus, OrderEvent event, Order order) {
        StateMachine<OrderStatus, OrderEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.start();

        log.info("[SSM] - Estado inicial da máquina de estados: {}", stateMachine.getState().getId());

        stateMachine.getStateMachineAccessor().doWithAllRegions(accessor ->
                accessor.resetStateMachine(new DefaultStateMachineContext<>(currentStatus, null, null, null))
        );

        if (stateMachine.sendEvent(event)) {
            log.info("[SSM] - Transição de estado realizada com sucesso.");
        } else {
            log.warn("[SSM] - Nenhum evento cadastrado para o estado recebido.");
        }

        log.info("[SSM] - Estado atualizado. Estado atual após transição: {}", stateMachine.getState().getId());

        if (stateMachine.getState().getId() == OrderStatus.VALIDATED) {
            log.info("[SSM] - Evento de validação identificado. Enviando pedido para processamento.");
            kafkaProcessedOrderProducer.sendProcessedOrder(order);
        }
    }
}
