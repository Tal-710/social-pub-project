package com.cyberpro.social_pub_project.repository;

import com.cyberpro.social_pub_project.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
