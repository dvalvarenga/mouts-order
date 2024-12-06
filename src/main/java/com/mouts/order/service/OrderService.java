package com.mouts.order.service;

import com.mouts.order.record.OrderRecord;
import com.mouts.order.entity.Order;
import com.mouts.order.enums.OrderEvent;
import com.mouts.order.enums.OrderStatus;
import com.mouts.order.exception.DuplicateOrderException;
import com.mouts.order.exception.OrderValidationException;
import com.mouts.order.messaging.KafkaOrderProducer;
import com.mouts.order.repository.OrderRepository;
import com.mouts.order.util.OrderUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStateMachineService stateMachineService;
    private final OrderUtil orderUtil;
    private final KafkaOrderProducer kafkaOrderProducer;

    public OrderService(OrderRepository orderRepository, OrderStateMachineService stateMachineService, OrderUtil orderUtil, KafkaOrderProducer kafkaOrderProducer) {
        this.orderRepository = orderRepository;
        this.stateMachineService = stateMachineService;
        this.orderUtil = orderUtil;
        this.kafkaOrderProducer = kafkaOrderProducer;
    }

    @Transactional
    @CacheEvict(value = "orders", allEntries = true)
    public void validateOrder(Order order) {
        log.info("Novo pedido recebido para cálculo e validação.");

        try {

            BigDecimal totalValue = calculateTotalAmount(order);
            order.setTotalAmount(totalValue);

            String orderHash = orderUtil.generateOrderHash(order);
            order.setOrderCode(orderHash);

            associateProductsWithOrder(order);

            order.setStatus(OrderStatus.VALIDATED);
            orderRepository.save(order);
            log.info("Pedido {} gerado com sucesso. Valor total do pedido: {}. Código de validação {}.", order.getId(), order.getTotalAmount(), order.getOrderCode());

            stateMachineService.changeOrderStatus(order.getStatus(), OrderEvent.VALIDATE, order);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateOrderException(order.getOrderCode());
        } catch (Exception e) {
            throw new OrderValidationException("Erro ao validar o pedido.", e);
        }
    }

    private BigDecimal calculateTotalAmount(Order order) {
        return order.getProducts().stream()
                .map(orderProduct -> orderProduct.getUnitPrice()
                        .multiply(BigDecimal.valueOf(orderProduct.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void associateProductsWithOrder(Order order) {
        order.getProducts().forEach(orderProduct -> orderProduct.setOrder(order));
    }

    public List<OrderRecord> getAllOrdersAsRecords() {
       log.info("Dados retornados da base de dados.");
        return orderRepository.findAll().stream()
                .map(orderUtil::toOrderRecord)
                .collect(Collectors.toList());
    }

    public void newOrder(String message){
        kafkaOrderProducer.sendToPendingOrders(message);
    }

}
