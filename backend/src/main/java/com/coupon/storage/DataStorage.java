package com.coupon.storage;

import com.coupon.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStorage {
    private static DataStorage instance;
    
    public final Map<String, CouponActivity> couponActivities = new ConcurrentHashMap<>();
    public final Map<String, UserCoupon> userCoupons = new ConcurrentHashMap<>();
    public final Map<String, User> users = new ConcurrentHashMap<>();
    public final Map<String, Blacklist> blacklist = new ConcurrentHashMap<>();
    public final Map<String, RiskRecord> riskRecords = new ConcurrentHashMap<>();
    public final Map<String, List<Long>> userReceiveTimestamps = new ConcurrentHashMap<>();
    
    private DataStorage() {}
    
    public static synchronized DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }
}
