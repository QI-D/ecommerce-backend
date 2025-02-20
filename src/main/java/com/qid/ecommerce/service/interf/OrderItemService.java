package com.qid.ecommerce.service.interf;

import com.qid.ecommerce.dto.OrderRequest;
import com.qid.ecommerce.dto.Response;
import com.qid.ecommerce.enums.OrderStatus;
import org.springframework.data.domain.Pageable;


import java.time.LocalDateTime;

public interface OrderItemService {
    Response placeOrder(OrderRequest orderRequest);
    Response updateOrderItemStatus(Long orderItemId, String status);
    Response filterOrderItems(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate, Long itemId, Pageable pageable);
}
