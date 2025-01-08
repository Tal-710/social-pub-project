package com.cyberpro.social_pub_project.repository;

import com.cyberpro.social_pub_project.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}