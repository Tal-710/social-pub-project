package com.cyberpro.social_pub_project.controller;


import com.cyberpro.social_pub_project.entity.Order;
import com.cyberpro.social_pub_project.service.OrderService;
import com.cyberpro.social_pub_project.service.OrderServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderServiceImpl orderService;

    public OrderController(OrderServiceImpl orderService1) {
        this.orderService = orderService1;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) {
        Optional<Order> order = orderService.findById(id);
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.save(order));
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Integer id) {
        orderService.deleteById(id);
    }
}

