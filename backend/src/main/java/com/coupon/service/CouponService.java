package com.coupon.service;

import com.coupon.dto.ReceiveRequest;
import com.coupon.dto.UseRequest;
import com.coupon.entity.*;
import com.coupon.storage.DataStorage;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CouponService {
    private final DataStorage storage = DataStorage.getInstance();

    public CouponActivity createActivity(CouponActivity activity) {
        activity.setId(UUID.randomUUID().toString());
        activity.setReceivedCount(0);
        activity.setUsedCount(0);
        activity.setExpiredCount(0);
        activity.setUsedBudget(BigDecimal.ZERO);
        activity.setCreatedAt(LocalDateTime.now());
        if (activity.getStackType() == null) {
            activity.setStackType("MUTUALLY_EXCLUSIVE");
        }
        if (activity.getPriority() == null) {
            activity.setPriority(1);
        }
        if (activity.getMutuallyExclusiveWith() == null) {
            activity.setMutuallyExclusiveWith(new ArrayList<>());
        }
        if (activity.getApplicableProducts() == null) {
            activity.setApplicableProducts(new ArrayList<>());
        }
        storage.couponActivities.put(activity.getId(), activity);
        return activity;
    }

    public List<CouponActivity> getAllActivities() {
        return new ArrayList<>(storage.couponActivities.values());
    }

    public CouponActivity getActivityById(String id) {
        return storage.couponActivities.get(id);
    }

    public UserCoupon receiveCoupon(ReceiveRequest request) {
        String userId = request.getUserId();
        String activityId = request.getActivityId();
        
        CouponActivity activity = storage.couponActivities.get(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartTime())) {
            throw new RuntimeException("活动未开始");
        }
        if (now.isAfter(activity.getEndTime())) {
            throw new RuntimeException("活动已结束");
        }

        if (activity.getReceivedCount() >= activity.getTotalStock()) {
            throw new RuntimeException("库存不足");
        }

        if (activity.getUsedBudget().compareTo(activity.getBudget()) >= 0) {
            throw new RuntimeException("活动预算已用完");
        }

        User user = storage.users.get(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (user.getLevel() < activity.getMinUserLevel()) {
            throw new RuntimeException("用户等级不足");
        }

        long userReceiveCount = storage.userCoupons.values().stream()
                .filter(uc -> uc.getUserId().equals(userId) && uc.getActivityId().equals(activityId))
                .count();
        if (userReceiveCount >= activity.getMaxReceivePerUser()) {
            throw new RuntimeException("用户已达到最大领取数量");
        }

        if (activity.getHighValue() && storage.blacklist.containsKey(userId)) {
            throw new RuntimeException("黑名单用户不能领取高价值优惠券");
        }

        checkAbnormalReceive(userId, activityId);

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setId(UUID.randomUUID().toString());
        userCoupon.setUserId(userId);
        userCoupon.setActivityId(activityId);
        userCoupon.setActivityName(activity.getName());
        userCoupon.setCouponValue(activity.getCouponValue());
        userCoupon.setMinOrderAmount(activity.getMinOrderAmount());
        userCoupon.setStatus("AVAILABLE");
        userCoupon.setReceiveTime(now);
        userCoupon.setExpireTime(activity.getEndTime());
        storage.userCoupons.put(userCoupon.getId(), userCoupon);

        activity.setReceivedCount(activity.getReceivedCount() + 1);

        return userCoupon;
    }

    private void checkAbnormalReceive(String userId, String activityId) {
        String key = userId + "_" + activityId;
        long now = System.currentTimeMillis();
        List<Long> timestamps = storage.userReceiveTimestamps.computeIfAbsent(key, k -> new ArrayList<>());
        
        timestamps.removeIf(t -> now - t > 60000);
        timestamps.add(now);
        
        if (timestamps.size() >= 5) {
            RiskRecord record = new RiskRecord();
            record.setId(UUID.randomUUID().toString());
            record.setUserId(userId);
            record.setActivityId(activityId);
            record.setRiskType("ABNORMAL_RECEIVE");
            record.setDescription("异常高频领取: " + timestamps.size() + "次/分钟，已触发风控拦截");
            record.setCreatedAt(LocalDateTime.now());
            storage.riskRecords.put(record.getId(), record);
            
            throw new RuntimeException("【风控拦截】检测到异常高频领取行为，1分钟内领取" + timestamps.size() + "次，已触发风控标记，用户ID: " + userId);
        }
    }

    public UserCoupon useCoupon(UseRequest request) {
        UserCoupon userCoupon = storage.userCoupons.get(request.getUserCouponId());
        if (userCoupon == null) {
            throw new RuntimeException("优惠券不存在");
        }

        if (!userCoupon.getUserId().equals(request.getUserId())) {
            throw new RuntimeException("优惠券不属于该用户");
        }

        if (!"AVAILABLE".equals(userCoupon.getStatus())) {
            throw new RuntimeException("优惠券状态不可用");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(userCoupon.getExpireTime())) {
            userCoupon.setStatus("EXPIRED");
            throw new RuntimeException("优惠券已过期");
        }

        if (request.getOrderAmount().compareTo(userCoupon.getMinOrderAmount()) < 0) {
            throw new RuntimeException("订单金额未达到使用门槛");
        }

        CouponActivity activity = storage.couponActivities.get(userCoupon.getActivityId());
        if (activity != null && activity.getApplicableProducts() != null && !activity.getApplicableProducts().isEmpty()) {
            boolean hasApplicableProduct = request.getProductIds().stream()
                    .anyMatch(p -> activity.getApplicableProducts().contains(p));
            if (!hasApplicableProduct) {
                throw new RuntimeException("订单中没有适用的商品");
            }
        }

        if (activity != null && activity.getUsedBudget().add(userCoupon.getCouponValue()).compareTo(activity.getBudget()) > 0) {
            throw new RuntimeException("活动预算不足，无法使用该优惠券");
        }

        userCoupon.setStatus("USED");
        userCoupon.setUsedTime(now);
        userCoupon.setOrderId(request.getOrderId());

        if (activity != null) {
            activity.setUsedCount(activity.getUsedCount() + 1);
            activity.setUsedBudget(activity.getUsedBudget().add(userCoupon.getCouponValue()));
        }

        return userCoupon;
    }

    public List<UserCoupon> getUserCoupons(String userId) {
        return storage.userCoupons.values().stream()
                .filter(uc -> uc.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        int totalReceived = storage.userCoupons.size();
        int totalUsed = (int) storage.userCoupons.values().stream()
                .filter(uc -> "USED".equals(uc.getStatus()))
                .count();
        int totalExpired = (int) storage.userCoupons.values().stream()
                .filter(uc -> "EXPIRED".equals(uc.getStatus()))
                .count();
        
        stats.put("totalReceived", totalReceived);
        stats.put("totalUsed", totalUsed);
        stats.put("totalExpired", totalExpired);
        stats.put("receiveRate", totalReceived > 0 ? String.format("%.2f%%", (double) totalReceived / storage.couponActivities.values().stream().mapToInt(CouponActivity::getTotalStock).sum() * 100) : "0.00%");
        stats.put("useRate", totalReceived > 0 ? String.format("%.2f%%", (double) totalUsed / totalReceived * 100) : "0.00%");
        stats.put("expireRate", totalReceived > 0 ? String.format("%.2f%%", (double) totalExpired / totalReceived * 100) : "0.00%");
        
        BigDecimal totalCost = storage.userCoupons.values().stream()
                .filter(uc -> "USED".equals(uc.getStatus()))
                .map(UserCoupon::getCouponValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalCost", totalCost);
        
        List<Map<String, Object>> activityStats = new ArrayList<>();
        for (CouponActivity activity : storage.couponActivities.values()) {
            Map<String, Object> as = new HashMap<>();
            as.put("id", activity.getId());
            as.put("name", activity.getName());
            as.put("totalStock", activity.getTotalStock());
            as.put("received", activity.getReceivedCount());
            as.put("used", activity.getUsedCount());
            as.put("expired", activity.getExpiredCount());
            as.put("budget", activity.getBudget());
            as.put("usedBudget", activity.getUsedBudget());
            activityStats.add(as);
        }
        stats.put("activities", activityStats);
        
        return stats;
    }

    public void addToBlacklist(Blacklist blacklistItem) {
        blacklistItem.setCreatedAt(LocalDateTime.now());
        storage.blacklist.put(blacklistItem.getUserId(), blacklistItem);
    }

    public void removeFromBlacklist(String userId) {
        storage.blacklist.remove(userId);
    }

    public List<Blacklist> getBlacklist() {
        return new ArrayList<>(storage.blacklist.values());
    }

    public List<RiskRecord> getRiskRecords() {
        return new ArrayList<>(storage.riskRecords.values());
    }

    public void addUser(User user) {
        storage.users.put(user.getId(), user);
    }

    public List<User> getUsers() {
        return new ArrayList<>(storage.users.values());
    }

    public void initTestData() {
        User user1 = new User();
        user1.setId("U001");
        user1.setName("测试用户1");
        user1.setLevel(1);
        storage.users.put(user1.getId(), user1);

        User user2 = new User();
        user2.setId("U002");
        user2.setName("测试用户2");
        user2.setLevel(2);
        storage.users.put(user2.getId(), user2);

        CouponActivity activity1 = new CouponActivity();
        activity1.setId("A001");
        activity1.setName("新用户专享券");
        activity1.setTotalStock(100);
        activity1.setReceivedCount(0);
        activity1.setUsedCount(0);
        activity1.setExpiredCount(0);
        activity1.setBudget(new BigDecimal("10000"));
        activity1.setUsedBudget(BigDecimal.ZERO);
        activity1.setMaxReceivePerUser(1);
        activity1.setMinUserLevel(1);
        activity1.setCouponValue(new BigDecimal("50"));
        activity1.setMinOrderAmount(new BigDecimal("200"));
        activity1.setHighValue(false);
        activity1.setCreatedAt(java.time.LocalDateTime.now());
        activity1.setStartTime(java.time.LocalDateTime.now().minusDays(1));
        activity1.setEndTime(java.time.LocalDateTime.now().plusDays(30));
        activity1.setApplicableProducts(java.util.Arrays.asList("PROD001", "PROD002"));
        activity1.setStackType("MUTUALLY_EXCLUSIVE");
        activity1.setPriority(1);
        activity1.setMutuallyExclusiveWith(new ArrayList<>());
        storage.couponActivities.put(activity1.getId(), activity1);

        CouponActivity activity2 = new CouponActivity();
        activity2.setId("A002");
        activity2.setName("VIP专属大额券");
        activity2.setTotalStock(50);
        activity2.setReceivedCount(0);
        activity2.setUsedCount(0);
        activity2.setExpiredCount(0);
        activity2.setBudget(new BigDecimal("20000"));
        activity2.setUsedBudget(BigDecimal.ZERO);
        activity2.setMaxReceivePerUser(1);
        activity2.setMinUserLevel(2);
        activity2.setCouponValue(new BigDecimal("200"));
        activity2.setMinOrderAmount(new BigDecimal("1000"));
        activity2.setHighValue(true);
        activity2.setCreatedAt(java.time.LocalDateTime.now());
        activity2.setStartTime(java.time.LocalDateTime.now().minusDays(1));
        activity2.setEndTime(java.time.LocalDateTime.now().plusDays(7));
        activity2.setApplicableProducts(new ArrayList<>());
        activity2.setStackType("MUTUALLY_EXCLUSIVE");
        activity2.setPriority(2);
        activity2.setMutuallyExclusiveWith(java.util.Arrays.asList("A001"));
        storage.couponActivities.put(activity2.getId(), activity2);
    }
}
