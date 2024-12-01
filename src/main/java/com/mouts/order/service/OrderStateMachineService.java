package com.mouts.order.service;

import com.mouts.order.enums.OrderEvent;
import com.mouts.order.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@Service
public class OrderStateMachineService {

    private final StateMachineFactory<OrderStatus, OrderEvent> stateMachineFactory;

    @Autowired
    public OrderStateMachineService(StateMachineFactory<OrderStatus, OrderEvent> stateMachineFactory) {
        this.stateMachineFactory = stateMachineFactory;
    }

    public void changeOrderStatus(OrderStatus currentStatus, OrderEvent event) {
        StateMachine<OrderStatus, OrderEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.start();

        stateMachine.getStateMachineAccessor().doWithAllRegions(accessor ->
                accessor.resetStateMachine(new DefaultStateMachineContext<>(currentStatus, null, null, null))
        );

        stateMachine.sendEvent(event);
    }
}
