<template>
  <div>
    <el-button type="primary" @click="showCreateDialog = true" style="margin-bottom: 20px">
      创建优惠券活动
    </el-button>

    <el-table :data="activities" border stripe>
      <el-table-column prop="name" label="活动名称" width="200" />
      <el-table-column prop="totalStock" label="总库存" width="80" />
      <el-table-column prop="receivedCount" label="已领取" width="80" />
      <el-table-column prop="usedCount" label="已使用" width="80" />
      <el-table-column prop="expiredCount" label="已过期" width="80" />
      <el-table-column prop="budget" label="预算" width="100" />
      <el-table-column prop="couponValue" label="优惠券金额" width="120" />
      <el-table-column prop="minOrderAmount" label="最低订单金额" width="120" />
      <el-table-column label="叠加类型" width="120">
        <template #default="{ row }">
          <el-tag :type="row.stackType === 'MUTUALLY_EXCLUSIVE' ? 'warning' : 'success'">
            {{ row.stackType === 'MUTUALLY_EXCLUSIVE' ? '互斥' : '可叠加' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="80">
        <template #default="{ row }">
          <el-tag type="primary">P{{ row.priority || 1 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="互斥活动" width="150">
        <template #default="{ row }">
          <el-tag v-if="!row.mutuallyExclusiveWith || row.mutuallyExclusiveWith.length === 0" type="info">无</el-tag>
          <span v-else>{{ row.mutuallyExclusiveWith.length }}个活动</span>
        </template>
      </el-table-column>
      <el-table-column label="适用商品" width="150">
        <template #default="{ row }">
          <el-tag v-if="!row.applicableProducts || row.applicableProducts.length === 0" type="info">全部商品</el-tag>
          <span v-else>{{ row.applicableProducts.length }}个商品</span>
        </template>
      </el-table-column>
      <el-table-column prop="highValue" label="高价值" width="80">
        <template #default="{ row }">
          <el-tag :type="row.highValue ? 'danger' : 'info'">
            {{ row.highValue ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showCreateDialog" title="创建优惠券活动" width="600px">
      <el-form :model="newActivity" label-width="140px">
        <el-form-item label="活动名称">
          <el-input v-model="newActivity.name" />
        </el-form-item>
        <el-form-item label="总库存">
          <el-input-number v-model="newActivity.totalStock" :min="1" />
        </el-form-item>
        <el-form-item label="预算">
          <el-input-number v-model="newActivity.budget" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="优惠券金额">
          <el-input-number v-model="newActivity.couponValue" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="最低订单金额">
          <el-input-number v-model="newActivity.minOrderAmount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="每人最多领取">
          <el-input-number v-model="newActivity.maxReceivePerUser" :min="1" />
        </el-form-item>
        <el-form-item label="最低用户等级">
          <el-input-number v-model="newActivity.minUserLevel" :min="1" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="newActivity.startTime" type="datetime" placeholder="选择开始时间" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="newActivity.endTime" type="datetime" placeholder="选择结束时间" />
        </el-form-item>
        <el-form-item label="高价值优惠券">
          <el-switch v-model="newActivity.highValue" />
        </el-form-item>
        <el-form-item label="适用商品范围">
          <el-select v-model="newActivity.applicableProducts" multiple placeholder="选择适用商品（空=全部商品）" style="width: 100%">
            <el-option label="商品A（PROD001）" value="PROD001" />
            <el-option label="商品B（PROD002）" value="PROD002" />
            <el-option label="商品C（PROD003）" value="PROD003" />
            <el-option label="商品D（PROD004）" value="PROD004" />
            <el-option label="商品E（PROD005）" value="PROD005" />
          </el-select>
        </el-form-item>
        <el-form-item label="叠加类型">
          <el-radio-group v-model="newActivity.stackType">
            <el-radio label="MUTUALLY_EXCLUSIVE">互斥</el-radio>
            <el-radio label="STACKABLE">可叠加</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="优先级">
          <el-input-number v-model="newActivity.priority" :min="1" :max="10" />
          <span style="margin-left: 10px; color: #909399;">（数字越小优先级越高）</span>
        </el-form-item>
        <el-form-item label="互斥活动">
          <el-select v-model="newActivity.mutuallyExclusiveWith" multiple placeholder="选择与哪些活动互斥" style="width: 100%">
            <el-option v-for="activity in activities" :key="activity.id" :label="activity.name" :value="activity.id" />
          </el-select>
          <div style="margin-top: 5px; color: #909399; font-size: 12px;">注：互斥的优惠券不可同时使用</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="createActivity">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const activities = ref([])
const showCreateDialog = ref(false)
const newActivity = ref({
  name: '',
  totalStock: 100,
  budget: 10000,
  couponValue: 10,
  minOrderAmount: 50,
  maxReceivePerUser: 2,
  minUserLevel: 1,
  startTime: null,
  endTime: null,
  highValue: false,
  applicableProducts: [],
  stackType: 'MUTUALLY_EXCLUSIVE',
  priority: 1,
  mutuallyExclusiveWith: []
})

const loadActivities = async () => {
  try {
    const res = await axios.get('/api/coupon/activities')
    if (res.data.success) {
      activities.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载失败')
  }
}

const createActivity = async () => {
  try {
    const res = await axios.post('/api/coupon/activity', newActivity.value)
    if (res.data.success) {
      ElMessage.success('创建成功')
      showCreateDialog.value = false
      loadActivities()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error('创建失败')
  }
}

onMounted(() => {
  loadActivities()
})
</script>
