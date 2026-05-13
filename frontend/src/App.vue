<template>
  <el-container style="height: 100vh">
    <el-aside width="200px" style="background-color: #2E3440">
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#2E3440"
        text-color="#ECEFF4"
        active-text-color="#88C0D0"
      >
        <el-menu-item index="/activities">
          <el-icon><Tickets /></el-icon>
          <span>优惠券活动</span>
        </el-menu-item>
        <el-menu-item index="/user-coupons">
          <el-icon><Ticket /></el-icon>
          <span>用户优惠券</span>
        </el-menu-item>
        <el-menu-item index="/statistics">
          <el-icon><DataLine /></el-icon>
          <span>统计分析</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/products">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/blacklist">
          <el-icon><Lock /></el-icon>
          <span>黑名单</span>
        </el-menu-item>
        <el-menu-item index="/risk-records">
          <el-icon><Warning /></el-icon>
          <span>风控记录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background-color: #4C566A; color: white; font-size: 20px; display: flex; align-items: center">
        优惠券营销风控系统
      </el-header>
      <el-main style="background-color: #ECEFF4">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Tickets, Ticket, DataLine, User, Lock, Warning, Goods } from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
</style>
