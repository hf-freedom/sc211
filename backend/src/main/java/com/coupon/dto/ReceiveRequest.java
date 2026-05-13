package com.coupon.dto;

public class ReceiveRequest {
    private String userId;
    private String activityId;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getActivityId() { return activityId; }
    public void setActivityId(String activityId) { this.activityId = activityId; }
}
