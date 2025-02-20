package com.qid.ecommerce.service.impl;

import com.qid.ecommerce.dto.OrderItemDto;
import com.qid.ecommerce.dto.OrderRequest;
import com.qid.ecommerce.dto.Response;
import com.qid.ecommerce.entity.Order;
import com.qid.ecommerce.entity.OrderItem;
import com.qid.ecommerce.entity.Product;
import com.qid.ecommerce.entity.User;
import com.qid.ecommerce.enums.OrderStatus;
import com.qid.ecommerce.exception.NotFoundException;
import com.qid.ecommerce.mapper.EntityDtoMapper;
import com.qid.ecommerce.repository.OrderItemRepo;
import com.qid.ecommerce.repository.OrderRepo;
import com.qid.ecommerce.repository.ProductRepo;
import com.qid.ecommerce.service.interf.OrderItemService;
import com.qid.ecommerce.service.interf.UserService;
import com.qid.ecommerce.specification.OrderItemSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final ProductRepo productRepo;
    private final UserService userService;
    private final EntityDtoMapper entityDtoMapper;

    @Override
    public Response placeOrder(OrderRequest orderRequest) {

        User user = userService.getLoginUser();

        // map order request items to order entities
        List<OrderItem> orderItems = orderRequest.getItems().stream().map(orderItemRequest -> {
            Product product = productRepo.findById(orderItemRequest.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(orderItemRequest.getQuantity());
            orderItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(orderItemRequest.getQuantity())));
            orderItem.setStatus(OrderStatus.PENDING);
            orderItem.setUser(user);

            return orderItem;
        }).collect(Collectors.toList());

        // calculate total price
        BigDecimal totalPrice = orderRequest.getTotalPrice() != null && orderRequest.getTotalPrice()
                .compareTo(BigDecimal.ZERO) > 0
                ? orderRequest.getTotalPrice()
                : orderItems.stream().map(OrderItem::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);

        // create order entity
        Order order = new Order();
        order.setOrderItemList(orderItems);
        order.setTotalPrice(totalPrice);

        // set the order reference in each order item
        orderItems.forEach(orderItem -> orderItem.setOrder(order));

        orderRepo.save(order);

        return Response.builder()
                .status(200)
                .message("Order placed successfully")
                .build();
    }

    @Override
    public Response updateOrderItemStatus(Long orderItemId, String status) {
        OrderItem orderItem = orderItemRepo.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("Order item not found"));

        orderItem.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        orderItemRepo.save(orderItem);

        return Response.builder()
                .status(200)
                .message("Order updated successfully")
                .build();
    }

    @Override
    public Response filterOrderItems(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate, Long itemId, Pageable pageable) {
        Specification<OrderItem> spec = Specification.where(OrderItemSpecification.hasStatus(status))
                .and(OrderItemSpecification.createdBetween(startDate, endDate))
                .and(OrderItemSpecification.hasItemId(itemId));

        Page<OrderItem> orderItemPage = orderItemRepo.findAll(spec, pageable);

        if (orderItemPage.isEmpty()) {
            throw new NotFoundException("No order found");
        }

        List<OrderItemDto> orderItemDtos = orderItemPage.getContent().stream()
                .map(entityDtoMapper::mapOrderItemToDtoPlusProductAndUser)
                .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .orderItemList(orderItemDtos)
                .totalPage(orderItemPage.getTotalPages())
                .totalElement(orderItemPage.getTotalElements())
                .build();
    }
}
