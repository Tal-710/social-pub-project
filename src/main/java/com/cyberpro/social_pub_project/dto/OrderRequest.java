package com.cyberpro.social_pub_project.dto;


import com.cyberpro.social_pub_project.entity.OrderDetail;
import com.cyberpro.social_pub_project.entity.User;
import java.util.List;

public class OrderRequest {
    private Integer userId;
    private List<OrderDetailRequest> orderDetails;
    private Double totalPrice;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<OrderDetailRequest> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailRequest> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}

