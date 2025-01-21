package com.cyberpro.social_pub_project.controller;


import com.cyberpro.social_pub_project.dto.OrderRequest;
import com.cyberpro.social_pub_project.entity.Order;
import com.cyberpro.social_pub_project.entity.OrderDetail;
import com.cyberpro.social_pub_project.entity.Product;
import com.cyberpro.social_pub_project.service.OrderService;
import com.cyberpro.social_pub_project.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders/")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService1) {
        this.orderService = orderService1;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.findAll();
        if (orders.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No orders found");
        }
        return ResponseEntity.ok(orders);
    }

    @GetMapping("user/{userId}/recent-products")
    public ResponseEntity<List<Product>> getRecentUniqueProducts(@PathVariable Integer userId) {
        List<Product> recentProducts = orderService.findLastFiveUniqueProducts(userId);
        if (recentProducts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(recentProducts);
    }

    @GetMapping("{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) {
        Optional<Order> order = orderService.findById(id);
        if (order.isPresent()) {
            return ResponseEntity.ok(order.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Order not found with id: " + id);
        }
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest)
    {
        Order order = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Integer id) {
        Optional<Order> order = orderService.findById(id);
        if (order.isPresent()) {
            try {
                orderService.deleteById(id);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error deleting order: " + e.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Order not found with id: " + id);
        }
    }
}

