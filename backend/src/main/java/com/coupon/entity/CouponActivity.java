package com.coupon.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CouponActivity {
    private String id;
    private String name;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalStock;
    private Integer receivedCount;
    private Integer usedCount;
    private Integer expiredCount;
    private BigDecimal budget;
    private BigDecimal usedBudget;
    private Integer maxReceivePerUser;
    private Integer minUserLevel;
    private BigDecimal couponValue;
    private BigDecimal minOrderAmount;
    private List<String> applicableProducts;
    private String stackType;
    private Integer priority;
    private List<String> mutuallyExclusiveWith;
    private Boolean highValue;
    private LocalDateTime createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Integer getTotalStock() { return totalStock; }
    public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }
    public Integer getReceivedCount() { return receivedCount; }
    public void setReceivedCount(Integer receivedCount) { this.receivedCount = receivedCount; }
    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
    public Integer getExpiredCount() { return expiredCount; }
    public void setExpiredCount(Integer expiredCount) { this.expiredCount = expiredCount; }
    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public BigDecimal getUsedBudget() { return usedBudget; }
    public void setUsedBudget(BigDecimal usedBudget) { this.usedBudget = usedBudget; }
    public Integer getMaxReceivePerUser() { return maxReceivePerUser; }
    public void setMaxReceivePerUser(Integer maxReceivePerUser) { this.maxReceivePerUser = maxReceivePerUser; }
    public Integer getMinUserLevel() { return minUserLevel; }
    public void setMinUserLevel(Integer minUserLevel) { this.minUserLevel = minUserLevel; }
    public BigDecimal getCouponValue() { return couponValue; }
    public void setCouponValue(BigDecimal couponValue) { this.couponValue = couponValue; }
    public BigDecimal getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(BigDecimal minOrderAmount) { this.minOrderAmount = minOrderAmount; }
    public List<String> getApplicableProducts() { return applicableProducts; }
    public void setApplicableProducts(List<String> applicableProducts) { this.applicableProducts = applicableProducts; }
    public String getStackType() { return stackType; }
    public void setStackType(String stackType) { this.stackType = stackType; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public List<String> getMutuallyExclusiveWith() { return mutuallyExclusiveWith; }
    public void setMutuallyExclusiveWith(List<String> mutuallyExclusiveWith) { this.mutuallyExclusiveWith = mutuallyExclusiveWith; }
    public Boolean getHighValue() { return highValue; }
    public void setHighValue(Boolean highValue) { this.highValue = highValue; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
