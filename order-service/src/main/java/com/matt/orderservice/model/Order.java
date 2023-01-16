package com.matt.orderservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name="`order`")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private UUID orderNumber;
  @OneToMany(cascade = CascadeType.ALL)
  private List<OrderLineItem> orderLineItemList;
}
