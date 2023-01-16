package com.matt.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_line_item")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderLineItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String skuCode;
  private BigDecimal price;
  private Integer quantity;
}
