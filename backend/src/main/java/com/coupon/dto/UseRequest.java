package com.coupon.dto;

import java.math.BigDecimal;
import java.util.List;

public class UseRequest {
    private String userId;
    private String userCouponId;
    private BigDecimal orderAmount;
    private List<String> productIds;
    private String orderId;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserCouponId() { return userCouponId; }
    public void setUserCouponId(String userCouponId) { this.userCouponId = userCouponId; }
    public BigDecimal getOrderAmount() { return orderAmount; }
    public void setOrderAmount(BigDecimal orderAmount) { this.orderAmount = orderAmount; }
    public List<String> getProductIds() { return productIds; }
    public void setProductIds(List<String> productIds) { this.productIds = productIds; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
}
