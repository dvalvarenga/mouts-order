package com.mouts.order.record;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record OrderProductRecord(
        Long productId,
        BigDecimal productPrice,
        BigDecimal orderProductPrice,
        Integer quantity,
        BigDecimal discountPercentage
) implements Serializable {

    public OrderProductRecord(Long productId, BigDecimal productPrice, BigDecimal orderProductPrice, Integer quantity) {
        this(
                productId,
                productPrice,
                orderProductPrice,
                quantity,
                calculateDiscountPercentage(productPrice, orderProductPrice)
        );
    }

    private static BigDecimal calculateDiscountPercentage(BigDecimal productPrice, BigDecimal orderProductPrice) {
        return productPrice.subtract(orderProductPrice)
                    .divide(productPrice, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
    }
}
