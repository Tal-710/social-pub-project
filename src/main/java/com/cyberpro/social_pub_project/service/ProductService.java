package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> findAll();
    List<Product> findValidProducts();

    Optional<Product> findById(int theId);

    Product save(Product theEmployee);

    void deleteById(int theId);
}
