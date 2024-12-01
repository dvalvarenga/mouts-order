package com.mouts.order.util;

import com.mouts.order.dto.OrderDTO;
import com.mouts.order.dto.OrderProductDTO;
import com.mouts.order.entity.Order;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderUtil {

    public OrderDTO toOrderDTO(Order order) {
        // Mapear os produtos do pedido para OrderProductDTO
        List<OrderProductDTO> productDTOs = order.getProducts().stream()
                .map(orderProduct -> {
                    return new OrderProductDTO(
                            orderProduct.getProduct().getId(),
                            orderProduct.getProduct().getName(),
                            orderProduct.getProduct().getPrice(),
                            orderProduct.getUnitPrice(),
                            orderProduct.getQuantity()
                    );
                }).collect(Collectors.toList());

        // Retornar o DTO do pedido com os produtos mapeados
        return new OrderDTO(
                order.getId(), // ID do pedido
                order.getTotalAmount(), // Valor total do pedido
                order.getStatus().name(), // Status do pedido
                productDTOs // Lista de produtos
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
}
