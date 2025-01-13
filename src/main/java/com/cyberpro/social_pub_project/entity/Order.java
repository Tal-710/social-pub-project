package com.cyberpro.social_pub_project.entity;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.List;


@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(name = "order_date", nullable = false, updatable = false)
    private java.sql.Timestamp orderDate;

    public Order(){

    }

    public Order(Integer id, User user, Product product, Double totalPrice, int quantity, Timestamp orderDate) {
        this.id = id;
        this.user = user;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }


    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }
}
