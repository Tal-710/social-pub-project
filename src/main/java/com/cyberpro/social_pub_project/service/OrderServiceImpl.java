package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.dto.OrderDetailRequest;
import com.cyberpro.social_pub_project.dto.OrderRequest;
import com.cyberpro.social_pub_project.entity.Order;
import com.cyberpro.social_pub_project.entity.*;
import com.cyberpro.social_pub_project.repository.OrderRepository;
import com.cyberpro.social_pub_project.repository.ProductRepository;
import com.cyberpro.social_pub_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final  UserRepository userRepository;
    private final  ProductRepository productRepository;
    private final IdEncryptionService  idEncryptionService ;
    @Autowired
    public OrderServiceImpl (OrderRepository orderRepository , UserRepository userRepository , ProductRepository productRepository, IdEncryptionService  idEncryptionService ){
        this.orderRepository = orderRepository;
        this.productRepository =productRepository;
        this.userRepository = userRepository;
        this.idEncryptionService  = idEncryptionService ;
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
    @Transactional(readOnly = true)
    @Override
    public List<Order> findAllByUserId(Integer userId) {
        return orderRepository.findAllByUserIdOrderByOrderDateDesc(userId);
    }

    public Order createOrder(OrderRequest orderRequest) {
        String encryptedId = idEncryptionService.encryptId(orderRequest.getUserId());
        User user = userRepository.findByEncryptedIdNumber(encryptedId)
                .orElseThrow(() -> new RuntimeException("User not found with ID Number: " + orderRequest.getUserId()));

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));

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
