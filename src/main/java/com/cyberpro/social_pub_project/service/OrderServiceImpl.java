package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.dto.OrderDetailRequest;
import com.cyberpro.social_pub_project.dto.OrderRequest;
import com.cyberpro.social_pub_project.entity.Order;
import com.cyberpro.social_pub_project.entity.*;
import com.cyberpro.social_pub_project.repository.OrderRepository;
import com.cyberpro.social_pub_project.repository.ProductRepository;
import com.cyberpro.social_pub_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final  UserRepository userRepository;
    private final  ProductRepository productRepository;
    @Autowired
    public OrderServiceImpl (OrderRepository orderRepository , UserRepository userRepository ,ProductRepository productRepository){
        this.orderRepository = orderRepository;
        this.productRepository =productRepository;
        this.userRepository = userRepository;

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

    @Override
    public List<Product> findLastFiveUniqueProducts(Integer userId) {
        return orderRepository.findLast5UniqueProductsByUserId(userId);
    }

    public Order createOrder(OrderRequest orderRequest) {
        User user = userRepository.findByIdNumber(orderRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID Number: " + orderRequest.getUserId()));

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));

        // Save the order first to get its ID
        order = orderRepository.save(order);

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailRequest detailRequest : orderRequest.getOrderDetails()) {
            Product product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + detailRequest.getProductId()));

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(detailRequest.getQuantity());
            orderDetail.setUnitPrice(detailRequest.getUnitPrice());
            orderDetail.setSubtotal(detailRequest.getTotalPrice());

            orderDetails.add(orderDetail);
        }

        order.setOrderDetails(orderDetails);
        return orderRepository.save(order);
    }

}
