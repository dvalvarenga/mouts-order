package com.mouts.order.service;

import com.mouts.order.dto.OrderDTO;
import com.mouts.order.entity.Order;
import com.mouts.order.enums.OrderEvent;
import com.mouts.order.enums.OrderStatus;
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

        BigDecimal totalValue = order.getProducts().stream()
                .map(orderProduct -> orderProduct.getUnitPrice()
                        .multiply(BigDecimal.valueOf(orderProduct.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(totalValue);

        try {
            stateMachineService.changeOrderStatus(order.getStatus(), OrderEvent.VALIDATE);
            order.setOrderCode(orderUtil.generateOrderHash(order));
            order.getProducts().forEach(orderProduct -> orderProduct.setOrder(order));
            order.setStatus(OrderStatus.VALIDATED);
            orderRepository.save(order);
            System.out.println("Pedido validado com sucesso!");
         } catch (DataIntegrityViolationException e) {
            System.out.println("Pedido duplicado.");
            throw new RuntimeException(e);
        }
    }

    public List<OrderDTO> getAllOrdersAsDTO() {
        return orderRepository.findAll().stream()
                .map(orderUtil::toOrderDTO)
                .collect(Collectors.toList());
    }
}
