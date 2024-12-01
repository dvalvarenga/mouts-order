package com.mouts.order.service;

import com.mouts.order.entity.Order;
import com.mouts.order.entity.OrderProduct;
import com.mouts.order.enums.OrderStatus;
import com.mouts.order.repository.OrderRepository;
import com.mouts.order.util.OrderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderStateMachineService stateMachineService;
    @Mock
    private OrderUtil orderUtil;
    @Mock
    private OrderRepository orderRepository;


    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateOrder_ShouldCalculateTotal() {

        OrderProduct orderProduct1 = new OrderProduct();
        orderProduct1.setQuantity(2);
        orderProduct1.setUnitPrice(BigDecimal.valueOf(50));

        OrderProduct orderProduct2 = new OrderProduct();
        orderProduct2.setQuantity(1);
        orderProduct2.setUnitPrice(BigDecimal.valueOf(100));

        Order order = new Order();
        order.setProducts(Arrays.asList(orderProduct1, orderProduct2));
        order.setStatus(OrderStatus.PENDING);

        orderService.validateOrder(order);

        assertEquals(OrderStatus.VALIDATED, order.getStatus());
    }
}
