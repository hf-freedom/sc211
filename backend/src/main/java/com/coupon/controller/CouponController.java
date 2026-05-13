package com.coupon.controller;

import com.coupon.dto.ReceiveRequest;
import com.coupon.dto.UseRequest;
import com.coupon.entity.*;
import com.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/activity")
    public Map<String, Object> createActivity(@RequestBody CouponActivity activity) {
        Map<String, Object> result = new HashMap<>();
        try {
            CouponActivity created = couponService.createActivity(activity);
            result.put("success", true);
            result.put("data", created);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/activities")
    public Map<String, Object> getAllActivities() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<CouponActivity> activities = couponService.getAllActivities();
            result.put("success", true);
            result.put("data", activities);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/activity/{id}")
    public Map<String, Object> getActivityById(@PathVariable String id) {
        Map<String, Object> result = new HashMap<>();
        try {
            CouponActivity activity = couponService.getActivityById(id);
            result.put("success", true);
            result.put("data", activity);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/receive")
    public Map<String, Object> receiveCoupon(@RequestBody ReceiveRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserCoupon userCoupon = couponService.receiveCoupon(request);
            result.put("success", true);
            result.put("data", userCoupon);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/use")
    public Map<String, Object> useCoupon(@RequestBody UseRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            UserCoupon userCoupon = couponService.useCoupon(request);
            result.put("success", true);
            result.put("data", userCoupon);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/user/{userId}")
    public Map<String, Object> getUserCoupons(@PathVariable String userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<UserCoupon> coupons = couponService.getUserCoupons(userId);
            result.put("success", true);
            result.put("data", coupons);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> stats = couponService.getStatistics();
            result.put("success", true);
            result.put("data", stats);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/blacklist")
    public Map<String, Object> addToBlacklist(@RequestBody Blacklist blacklistItem) {
        Map<String, Object> result = new HashMap<>();
        try {
            couponService.addToBlacklist(blacklistItem);
            result.put("success", true);
            result.put("message", "添加成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/blacklist/{userId}")
    public Map<String, Object> removeFromBlacklist(@PathVariable String userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            couponService.removeFromBlacklist(userId);
            result.put("success", true);
            result.put("message", "移除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/blacklist")
    public Map<String, Object> getBlacklist() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Blacklist> blacklist = couponService.getBlacklist();
            result.put("success", true);
            result.put("data", blacklist);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/risk-records")
    public Map<String, Object> getRiskRecords() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<RiskRecord> records = couponService.getRiskRecords();
            result.put("success", true);
            result.put("data", records);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/user")
    public Map<String, Object> addUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            couponService.addUser(user);
            result.put("success", true);
            result.put("message", "添加成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/users")
    public Map<String, Object> getUsers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<User> users = couponService.getUsers();
            result.put("success", true);
            result.put("data", users);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/init-test-data")
    public Map<String, Object> initTestData() {
        Map<String, Object> result = new HashMap<>();
        try {
            couponService.initTestData();
            result.put("success", true);
            result.put("message", "测试数据初始化成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
