<template>
  <div>
    <el-card style="margin-bottom: 20px">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-select v-model="selectedUser" placeholder="选择用户" style="width: 100%" @change="loadUserCoupons">
            <el-option v-for="user in users" :key="user.id" :label="user.name" :value="user.id" />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-select v-model="selectedActivity" placeholder="选择活动" style="width: 100%">
            <el-option v-for="activity in activities" :key="activity.id" :label="activity.name" :value="activity.id" />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-button type="primary" @click="receiveCoupon" :disabled="!selectedUser || !selectedActivity">
            领取优惠券
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-table :data="userCoupons" border stripe>
      <el-table-column prop="activityName" label="活动名称" width="200" />
      <el-table-column prop="couponValue" label="优惠券金额" width="120" />
      <el-table-column prop="minOrderAmount" label="最低订单金额" width="150" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="receiveTime" label="领取时间" width="180" />
      <el-table-column prop="expireTime" label="过期时间" width="180" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button v-if="row.status === 'AVAILABLE'" type="primary" size="small" @click="useCoupon(row)">
            使用
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showUseDialog" title="使用优惠券" width="600px">
      <el-alert title="优惠券信息" type="info" style="margin-bottom: 20px">
        <template #default>
          <p>活动：{{ currentCoupon?.activityName }}</p>
          <p>优惠券面额：{{ currentCoupon?.couponValue }} 元</p>
          <p>最低订单金额：{{ currentCoupon?.minOrderAmount }} 元</p>
          <p>有效期至：{{ currentCoupon?.expireTime }}</p>
        </template>
      </el-alert>
      <el-form :model="useForm" label-width="120px">
        <el-form-item label="订单金额" required>
          <el-input-number v-model="useForm.orderAmount" :min="0" :precision="2" style="width: 200px" />
          <span style="margin-left: 10px; color: #909399">
            需满足最低：{{ currentCoupon?.minOrderAmount || 0 }} 元
            <span v-if="useForm.orderAmount < (currentCoupon?.minOrderAmount || 0)" style="color: #F56C6C">（不满足最低消费门槛）</span>
          </span>
        </el-form-item>
        <el-form-item label="选择商品">
          <el-select v-model="useForm.productIds" multiple placeholder="请选择商品（至少选择适用商品）" style="width: 100%">
            <el-option label="商品A（PROD001）" value="PROD001" />
            <el-option label="商品B（PROD002）" value="PROD002" />
            <el-option label="商品C（PROD003）" value="PROD003" />
            <el-option label="商品D（PROD004）" value="PROD004" />
            <el-option label="商品E（PROD005）" value="PROD005" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单ID">
          <el-input v-model="useForm.orderId" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="优惠后金额">
          <span style="font-size: 18px; font-weight: bold; color: #F56C6C">
            {{ Math.max(0, useForm.orderAmount - (currentCoupon?.couponValue || 0)).toFixed(2) }} 元
          </span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showUseDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmUse" :disabled="!canUseCoupon">确认使用</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const users = ref([])
const activities = ref([])
const userCoupons = ref([])
const selectedUser = ref('')
const selectedActivity = ref('')
const showUseDialog = ref(false)
const currentCoupon = ref(null)
const useForm = ref({
  orderAmount: 0,
  orderId: '',
  productIds: []
})

const canUseCoupon = computed(() => {
  if (!currentCoupon.value) return false
  if (useForm.value.orderAmount < currentCoupon.value.minOrderAmount) return false
  return true
})

const loadUsers = async () => {
  try {
    const res = await axios.get('/api/coupon/users')
    if (res.data.success) {
      users.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载用户失败')
  }
}

const loadActivities = async () => {
  try {
    const res = await axios.get('/api/coupon/activities')
    if (res.data.success) {
      activities.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载活动失败')
  }
}

const loadUserCoupons = async () => {
  if (!selectedUser.value) return
  try {
    const res = await axios.get(`/api/coupon/user/${selectedUser.value}`)
    if (res.data.success) {
      userCoupons.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载用户优惠券失败')
  }
}

const receiveCoupon = async () => {
  try {
    const res = await axios.post('/api/coupon/receive', {
      userId: selectedUser.value,
      activityId: selectedActivity.value
    })
    if (res.data.success) {
      ElMessage.success('领取成功')
      loadUserCoupons()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error('领取失败')
  }
}

const useCoupon = (coupon) => {
  currentCoupon.value = coupon
  useForm.value = {
    orderAmount: coupon.minOrderAmount,
    orderId: '',
    productIds: []
  }
  showUseDialog.value = true
}

const confirmUse = async () => {
  try {
    const res = await axios.post('/api/coupon/use', {
      userId: selectedUser.value,
      userCouponId: currentCoupon.value.id,
      orderAmount: useForm.value.orderAmount,
      orderId: useForm.value.orderId,
      productIds: useForm.value.productIds
    })
    if (res.data.success) {
      ElMessage.success('使用成功')
      showUseDialog.value = false
      loadUserCoupons()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error('使用失败')
  }
}

const getStatusType = (status) => {
  const map = {
    AVAILABLE: 'success',
    USED: 'info',
    EXPIRED: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    AVAILABLE: '可用',
    USED: '已使用',
    EXPIRED: '已过期'
  }
  return map[status] || status
}

onMounted(() => {
  loadUsers()
  loadActivities()
})
</script>
