package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.dto.OrderRequest;
import com.cyberpro.social_pub_project.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    List<Order> findAll();

    Optional<Order> findById(int theId);

    Order save(Order theEmployee);

    void deleteById(int theId);

    Order createOrder(OrderRequest orderRequest);
}
