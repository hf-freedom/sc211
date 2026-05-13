package com.coupon.task;

import com.coupon.entity.CouponActivity;
import com.coupon.entity.UserCoupon;
import com.coupon.storage.DataStorage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ExpireCouponTask {
    private final DataStorage storage = DataStorage.getInstance();

    @Scheduled(fixedRate = 60000)
    public void expireCoupons() {
        LocalDateTime now = LocalDateTime.now();
        for (UserCoupon userCoupon : storage.userCoupons.values()) {
            if ("AVAILABLE".equals(userCoupon.getStatus()) && now.isAfter(userCoupon.getExpireTime())) {
                userCoupon.setStatus("EXPIRED");
                
                CouponActivity activity = storage.couponActivities.get(userCoupon.getActivityId());
                if (activity != null) {
                    activity.setExpiredCount(activity.getExpiredCount() + 1);
                }
            }
        }
    }
}
