package com.cyberpro.social_pub_project.repository;

import com.cyberpro.social_pub_project.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE p.isValid = 1")
    List<Product> findValidProducts();

}
