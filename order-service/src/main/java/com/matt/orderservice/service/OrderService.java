package com.matt.orderservice.service;

import com.matt.orderservice.dto.OrderLineItemDto;
import com.matt.orderservice.dto.OrderRequest;
import com.matt.orderservice.model.Order;
import com.matt.orderservice.model.OrderLineItem;
import com.matt.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;

  public void placeOrder(OrderRequest orderRequest) {
    Order order =
        Order.builder()
            .orderNumber(UUID.randomUUID())
            .orderLineItemList(
                orderRequest.getOrderLineItemDtoList().stream().map(this::mapToDto).toList())
            .build();
    orderRepository.save(order);
  }

  private OrderLineItem mapToDto(OrderLineItemDto orderLineItemDto) {
    return OrderLineItem.builder()
        .price(orderLineItemDto.getPrice())
        .quantity(orderLineItemDto.getQuantity())
        .skuCode(orderLineItemDto.getSkuCode())
        .build();
  }
}
