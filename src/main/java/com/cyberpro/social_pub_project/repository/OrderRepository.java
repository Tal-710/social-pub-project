package com.cyberpro.social_pub_project.repository;

import com.cyberpro.social_pub_project.entity.Order;
import com.cyberpro.social_pub_project.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query(value = """
    SELECT * FROM products WHERE id IN (
        SELECT DISTINCT od.product_id
        FROM orders o
        JOIN order_details od ON o.id = od.order_id
        WHERE o.user_id = :userId
        ORDER BY o.order_date DESC
    ) LIMIT 5
    """, nativeQuery = true)
    List<Product> findLast5UniqueProductsByUserId(@Param("userId") Integer userId);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.product " +
            "WHERE o.user.id = :userId " +
            "ORDER BY o.orderDate DESC")
    List<Order> findAllByUserIdOrderByOrderDateDesc(@Param("userId") Integer userId);



}