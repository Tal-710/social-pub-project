package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.entity.Product;
import com.cyberpro.social_pub_project.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;


    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {

        this.productRepository =productRepository;
    }

    public List<Product> findAll() {

        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(int theId) {

        return productRepository.findById(theId);
    }

    @Override
    public Product save(Product theProduct) {
        return productRepository.save(theProduct);
    }

    @Override
    public void deleteById(int theId) {

        productRepository.deleteById(theId);

    }
}
