<template>
  <div>
    <el-card style="margin-bottom: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>商品列表（用于优惠券适用范围配置）</span>
          <el-button type="primary" size="small" @click="loadTestData">加载测试数据</el-button>
        </div>
      </template>
      <el-table :data="products" border stripe>
        <el-table-column prop="id" label="商品ID" width="150" />
        <el-table-column prop="name" label="商品名称" width="200" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">¥{{ row.price.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column label="状态" width="100">
          <el-tag type="success">在售</el-tag>
        </el-table-column>
      </el-table>
    </el-card>

    <el-alert title="使用说明" type="info" :closable="false">
      <template #default>
        <ol>
          <li>先在【用户管理】页面添加测试用户（或点击下方按钮加载）</li>
          <li>在【优惠券活动】页面创建活动，配置：总库存、预算、优惠券金额、最低订单金额、每人限领、适用商品范围等</li>
          <li>在【用户优惠券】页面：选择用户和活动 → 点击领取 → 领取成功后可查看优惠券列表</li>
          <li>在【用户优惠券】列表中点击"使用"按钮：输入订单金额（需满足最低门槛）、选择商品、订单号 → 确认使用</li>
          <li>在【统计分析】页面查看领取率、使用率、成本等数据</li>
        </ol>
      </template>
    </el-alert>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const products = ref([
  { id: 'PROD001', name: '商品A', price: 99.9, category: '电子产品' },
  { id: 'PROD002', name: '商品B', price: 199.9, category: '服装' },
  { id: 'PROD003', name: '商品C', price: 299.9, category: '家居' },
  { id: 'PROD004', name: '商品D', price: 399.9, category: '食品' },
  { id: 'PROD005', name: '商品E', price: 499.9, category: '美妆' }
])

const loadTestData = async () => {
  try {
    const res = await axios.post('/api/coupon/init-test-data')
    if (res.data.success) {
      ElMessage.success('测试数据已加载！请在各页面测试功能：2个用户、2个优惠券活动已创建')
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

onMounted(() => {})
</script>
