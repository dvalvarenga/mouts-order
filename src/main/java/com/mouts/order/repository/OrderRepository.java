package com.mouts.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mouts.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
