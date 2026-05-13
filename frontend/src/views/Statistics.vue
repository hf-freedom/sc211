<template>
  <div>
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 30px; color: #409EFF; font-weight: bold">{{ statistics.totalReceived || 0 }}</div>
            <div style="color: #909399; margin-top: 10px">总领取数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 30px; color: #67C23A; font-weight: bold">{{ statistics.totalUsed || 0 }}</div>
            <div style="color: #909399; margin-top: 10px">总使用数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 30px; color: #E6A23C; font-weight: bold">{{ statistics.totalExpired || 0 }}</div>
            <div style="color: #909399; margin-top: 10px">总过期数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 30px; color: #F56C6C; font-weight: bold">{{ statistics.totalCost || 0 }}</div>
            <div style="color: #909399; margin-top: 10px">总成本</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="8">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 24px; color: #409EFF; font-weight: bold">{{ statistics.receiveRate || '0.00%' }}</div>
            <div style="color: #909399; margin-top: 10px">领取率</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 24px; color: #67C23A; font-weight: bold">{{ statistics.useRate || '0.00%' }}</div>
            <div style="color: #909399; margin-top: 10px">使用率</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 24px; color: #F56C6C; font-weight: bold">{{ statistics.expireRate || '0.00%' }}</div>
            <div style="color: #909399; margin-top: 10px">过期率</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <span style="font-weight: bold">活动明细</span>
      </template>
      <el-table :data="statistics.activities || []" border stripe>
        <el-table-column prop="name" label="活动名称" width="200" />
        <el-table-column prop="totalStock" label="总库存" width="100" />
        <el-table-column prop="received" label="已领取" width="100" />
        <el-table-column prop="used" label="已使用" width="100" />
        <el-table-column prop="expired" label="已过期" width="100" />
        <el-table-column prop="budget" label="预算" width="120" />
        <el-table-column prop="usedBudget" label="已用预算" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const statistics = ref({})

const loadStatistics = async () => {
  try {
    const res = await axios.get('/api/coupon/statistics')
    if (res.data.success) {
      statistics.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载统计数据失败')
  }
}

onMounted(() => {
  loadStatistics()
})
</script>
