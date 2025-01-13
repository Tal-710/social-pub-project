package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.entity.Order;
import com.cyberpro.social_pub_project.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    @Autowired
    public OrderServiceImpl (OrderRepository orderRepository){
        this.orderRepository = orderRepository;

    }
    @Override
    public List<Order> findAll() {

        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> findById(int theId) {

        return orderRepository.findById(theId);
    }

    @Override
    public Order save(Order theEmployee) {

        return orderRepository.save(theEmployee);
    }

    @Override
    public void deleteById(int theId) {

        orderRepository.deleteById(theId);
    }
}
