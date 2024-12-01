package com.mouts.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private BigDecimal totalAmount;
    private String status;
    private List<OrderProductDTO> products;
}
