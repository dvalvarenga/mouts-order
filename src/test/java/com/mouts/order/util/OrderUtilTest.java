package com.mouts.order.util;

import com.mouts.order.entity.Order;
import com.mouts.order.entity.OrderProduct;
import com.mouts.order.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockitoExtension.class)
class OrderUtilTest {

    private OrderUtil orderUtil;

    @BeforeEach
    void setUp() {
        orderUtil = new OrderUtil();
    }

    @Test
    void generateOrderHash_Success() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Mouse");
        product.setPrice(BigDecimal.valueOf(50.00));

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setQuantity(2);
        orderProduct.setUnitPrice(BigDecimal.valueOf(45.00));

        Order order = new Order();
        order.setProducts(Collections.singletonList(orderProduct));
        order.setTotalAmount(BigDecimal.valueOf(90.00)); // Total: 2 x 45.00

        String hash1 = orderUtil.generateOrderHash(order);

        String hash2 = orderUtil.generateOrderHash(order);

        assertEquals(hash1, hash2, "O hash gerado não é consistente.");
    }


    @Test
    void generateOrderHash_DifferentOrders() {

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Mouse");
        product1.setPrice(BigDecimal.valueOf(50.00));

        OrderProduct orderProduct1 = new OrderProduct();
        orderProduct1.setProduct(product1);
        orderProduct1.setQuantity(2);
        orderProduct1.setUnitPrice(BigDecimal.valueOf(45.00));

        Order order1 = new Order();
        order1.setProducts(Collections.singletonList(orderProduct1));
        order1.setTotalAmount(BigDecimal.valueOf(90.00));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Teclado");
        product2.setPrice(BigDecimal.valueOf(100.00));

        OrderProduct orderProduct2 = new OrderProduct();
        orderProduct2.setProduct(product2);
        orderProduct2.setQuantity(1);
        orderProduct2.setUnitPrice(BigDecimal.valueOf(95.00));

        Order order2 = new Order();
        order2.setProducts(Collections.singletonList(orderProduct2));
        order2.setTotalAmount(BigDecimal.valueOf(95.00));

        String hash1 = orderUtil.generateOrderHash(order1);
        String hash2 = orderUtil.generateOrderHash(order2);

        assertNotEquals(hash1, hash2, "Hashes de pedidos diferentes não deveriam ser iguais.");
    }

}
