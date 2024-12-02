package com.mouts.order.util;

import com.mouts.order.enums.OrderStatus;
import com.mouts.order.record.OrderRecord;
import com.mouts.order.record.OrderProductRecord;
import com.mouts.order.entity.Order;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderUtil {

    public OrderRecord toOrderRecord(Order order) {

        List<OrderProductRecord> productRecords = order.getProducts().stream()
                .map(orderProduct -> {
                    return new OrderProductRecord(
                            orderProduct.getProduct().getId(),
                            orderProduct.getProduct().getName(),
                            orderProduct.getProduct().getPrice(),
                            orderProduct.getUnitPrice(),
                            orderProduct.getQuantity()
                    );
                }).collect(Collectors.toList());


        return new OrderRecord(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus().name(),
                productRecords
        );
    }

    public OrderRecord toOrderRecord(Order order,OrderStatus orderStatus) {

        List<OrderProductRecord> productRecords = order.getProducts().stream()
                .map(orderProduct -> {
                    return new OrderProductRecord(
                            orderProduct.getProduct().getId(),
                            orderProduct.getProduct().getName(),
                            orderProduct.getProduct().getPrice(),
                            orderProduct.getUnitPrice(),
                            orderProduct.getQuantity()
                    );
                }).collect(Collectors.toList());


        return new OrderRecord(
                order.getId(),
                order.getTotalAmount(),
                orderStatus.name(),
                productRecords
        );
    }

    public String generateOrderHash(Order order) {
        try {
            String dataToHash = order.getProducts().stream()
                    .map(orderProduct -> orderProduct.getProduct().getId() + "-" +
                            orderProduct.getQuantity() + "-" +
                            orderProduct.getUnitPrice()
                    )
                    .collect(Collectors.joining("|"));

            dataToHash += "|" + order.getTotalAmount();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(dataToHash.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash do pedido: " + e.getMessage(), e);
        }
    }

    public OrderRecord changerOrderRecordStatus(OrderRecord orderRecord, OrderStatus status){
        return  new OrderRecord(
                orderRecord.orderId(),
                orderRecord.totalAmount(),
                status.name(),
                orderRecord.products()
        );
    }
}
