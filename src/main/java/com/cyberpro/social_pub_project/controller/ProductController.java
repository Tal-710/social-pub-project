package com.cyberpro.social_pub_project.controller;

import com.cyberpro.social_pub_project.entity.Product;
import com.cyberpro.social_pub_project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
//@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:5500", "http://127.0.0.1:5500"},
//        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
//        allowedHeaders = "*",
//        allowCredentials = "true")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService1) {
        this.productService = productService1;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found");
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Product not found with id: " + id);
        }
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.save(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody Product product) {
        Optional<Product> existingProduct = productService.findById(id);
        if (existingProduct.isPresent()) {
            product.setId(id);  // Ensure the ID is set correctly
            return ResponseEntity.ok(productService.save(product));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Product not found with id: " + id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            try {
                productService.deleteById(id);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Error deleting product: " + e.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Product not found with id: " + id);
        }
    }
}

