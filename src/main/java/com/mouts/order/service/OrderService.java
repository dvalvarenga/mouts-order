package com.mouts.order.service;

import com.mouts.order.dto.OrderDTO;
import com.mouts.order.entity.Order;
import com.mouts.order.enums.OrderEvent;
import com.mouts.order.enums.OrderStatus;
import com.mouts.order.exception.DuplicateOrderException;
import com.mouts.order.exception.OrderValidationException;
import com.mouts.order.repository.OrderRepository;
import com.mouts.order.util.OrderUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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

    public OrderService(OrderRepository orderRepository, OrderStateMachineService stateMachineService, OrderUtil orderUtil) {
        this.orderRepository = orderRepository;
        this.stateMachineService = stateMachineService;
        this.orderUtil = orderUtil;
    }

    @Transactional
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

            log.info("Pedido {} gerado com sucesso. Valor total do pedido: {}. Código de validação {}.", order.getId(), order.getTotalAmount(),order.getOrderCode());

            stateMachineService.changeOrderStatus(order.getStatus(), OrderEvent.VALIDATE, order);
        } catch (DataIntegrityViolationException e) {
            log.error("Erro ao validar pedido: duplicado. ID: {} ", order.getOrderCode(), e);
            throw new DuplicateOrderException("Pedido duplicado: " + order.getOrderCode(), e);
        } catch (Exception e) {
            log.error("Erro de validação do pedido {} .", order.getOrderCode(), e);
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

    public List<OrderDTO> getAllOrdersAsDTO() {
        return orderRepository.findAll().stream()
                .map(orderUtil::toOrderDTO)
                .collect(Collectors.toList());
    }
}
