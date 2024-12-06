package com.qid.ecommerce.repository;

import com.qid.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {
}
