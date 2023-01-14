package com.matt.productservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matt.productservice.dto.ProductRequest;
import com.matt.productservice.dto.ProductResponse;
import com.matt.productservice.model.Product;
import com.matt.productservice.repository.ProductRepository;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.ModelResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

  @Container
  static final MongoDBContainer mongoDBContainer =
      new MongoDBContainer(DockerImageName.parse("mongo:4.4.2"));

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ProductRepository productRepository;

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
    dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @AfterEach
  void tearDown() {
    productRepository.deleteAll();
  }

  @Test
  void shouldCreateProduct() throws Exception {
    ProductRequest productRequest = getProductRequest();
    String productRequestString = objectMapper.writeValueAsString(productRequest);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productRequestString))
        .andExpect(status().isCreated());

    Assertions.assertEquals(1, productRepository.findAll().size());
  }

  @Test
  void shouldGetProducts() throws Exception {
    productRepository.save(
        Product.builder()
            .id("a123")
            .name("Phone")
            .description("A phone")
            .price(BigDecimal.valueOf(100))
            .build());

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/product"))
        .andExpect(
            MockMvcResultMatchers.content()
                .json(
                    "[{\"id\":\"a123\",\"name\":\"Phone\",\"description\":\"A phone\",\"price\":100}]"));
  }

  private ProductRequest getProductRequest() {
    return ProductRequest.builder()
        .name("Phone")
        .description("A phone")
        .price(BigDecimal.valueOf(100))
        .build();
  }
}
