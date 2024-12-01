package com.mouts.order.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record OrderProductDTO(
        Long productId,
        String productName,
        BigDecimal productPrice,
        BigDecimal orderProductPrice,
        Integer quantity,
        BigDecimal discountPercentage
) {
    public OrderProductDTO(Long productId, String productName, BigDecimal productPrice, BigDecimal orderProductPrice, Integer quantity) {
        this(
                productId,
                productName,
                productPrice,
                orderProductPrice,
                quantity,
                calculateDiscountPercentage(productPrice, orderProductPrice)
        );
    }

    private static BigDecimal calculateDiscountPercentage(BigDecimal productPrice, BigDecimal orderProductPrice) {
        if (productPrice == null || orderProductPrice == null || productPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return productPrice.subtract(orderProductPrice)
                .divide(productPrice, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }
}
