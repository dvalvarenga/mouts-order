package com.mouts.order.record;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record OrderProductRecord(
        Long productId,
        String productName,
        BigDecimal productPrice,
        BigDecimal orderProductPrice,
        Integer quantity,
        BigDecimal discountPercentage
) implements Serializable {

    public OrderProductRecord(Long productId, String productName, BigDecimal productPrice, BigDecimal orderProductPrice, Integer quantity) {
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
        return switch (productPrice.compareTo(orderProductPrice)) {
            case 0 -> BigDecimal.ZERO;
            default -> productPrice.subtract(orderProductPrice)
                    .divide(productPrice, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        };
    }
}
