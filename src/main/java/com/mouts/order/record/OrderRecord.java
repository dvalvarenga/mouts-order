package com.mouts.order.record;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public record OrderRecord(
        Long orderId,
        BigDecimal totalAmount,
        String status,
        List<OrderProductRecord> products
) implements Serializable {

}


