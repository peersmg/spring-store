package com.matt.orderservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matt.orderservice.dto.OrderLineItemDto;
import com.matt.orderservice.dto.OrderRequest;
import com.matt.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderServiceApplicationTests {

  @Autowired private MockMvc mockMvc;
  @Autowired private OrderRepository orderRepository;
  @Autowired private ObjectMapper objectMapper;

  @Container
  static final MySQLContainer<?> mySqlContainer =
      new MySQLContainer<>(DockerImageName.parse("mysql:8.0.31"));

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
    dynamicPropertyRegistry.add("spring.datasource.url", mySqlContainer::getJdbcUrl);
    dynamicPropertyRegistry.add("spring.datasource.username", mySqlContainer::getUsername);
    dynamicPropertyRegistry.add("spring.datasource.password", mySqlContainer::getPassword);
  }

  @AfterEach
  void tearDown() {
    orderRepository.deleteAll();
  }

  @Test
  void shouldCreateProduct() throws Exception {
    OrderRequest orderRequest = getOrderRequest();
    String orderRequestString = objectMapper.writeValueAsString(orderRequest);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequestString))
        .andExpect(status().isCreated());

    Assertions.assertEquals(1, orderRepository.findAll().size());
  }

  private OrderRequest getOrderRequest() {
    return OrderRequest.builder()
        .orderLineItemDtoList(
            List.of(
                OrderLineItemDto.builder()
                    .price(BigDecimal.valueOf(100))
                    .quantity(2)
                    .skuCode("Name")
                    .build()))
        .build();
  }
}
