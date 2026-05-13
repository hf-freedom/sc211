<template>
  <div>
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-weight: bold;">风控测试工具</span>
          <el-tag type="warning">ABNORMAL_RECEIVE: 1分钟内领取≥5次触发风控</el-tag>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form label-width="80px">
            <el-form-item label="选择用户">
              <el-select v-model="testUserId" placeholder="请选择用户" style="width: 100%">
                <el-option v-for="user in users" :key="user.id" :label="user.name" :value="user.id" />
              </el-select>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="8">
          <el-form label-width="80px">
            <el-form-item label="选择活动">
              <el-select v-model="testActivityId" placeholder="请选择活动" style="width: 100%">
                <el-option v-for="activity in activities" :key="activity.id" :label="activity.name" :value="activity.id" />
              </el-select>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="8">
          <el-form-item>
            <el-button type="primary" @click="triggerRiskTest" :loading="loading" :disabled="!testUserId || !testActivityId">
              触发风控测试（连续领取5次）
            </el-button>
            <el-button style="margin-left: 10px;" @click="loadRiskRecords">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
      <el-alert title="操作说明" type="info" :closable="false" style="margin-top: 10px;">
        <ul style="margin: 0; padding-left: 20px;">
          <li>请先在【商品管理】页面加载测试数据</li>
          <li>选择用户和活动后，点击"触发风控测试"按钮</li>
          <li>系统将模拟1分钟内连续领取5次优惠券</li>
          <li>第5次领取时会触发风控拦截并记录</li>
          <li>触发后可在下方风控记录列表查看记录</li>
        </ul>
      </el-alert>
    </el-card>

    <el-card v-if="riskRecords.length === 0">
      <el-empty description="暂无风控记录，可使用上方测试工具触发">
        <template #image>
          <el-icon size="80" color="#E6A23C"><Warning /></el-icon>
        </template>
      </el-empty>
    </el-card>

    <el-table v-else :data="riskRecords" border stripe>
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="riskType" label="风控类型" width="180">
        <template #default="{ row }">
          <el-tag type="danger" effect="dark">
            <el-icon style="margin-right: 5px;"><Warning /></el-icon>
            {{ row.riskType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="userId" label="用户ID" width="150" />
      <el-table-column prop="activityId" label="活动ID" width="150" />
      <el-table-column prop="description" label="风控描述" min-width="250">
        <template #default="{ row }">
          <el-tag type="warning">{{ row.description }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="触发时间" width="180" />
      <el-table-column label="操作" width="100">
        <template #default>
          <el-button type="danger" size="small" plain>标记处理</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Warning } from '@element-plus/icons-vue'

const riskRecords = ref([])
const users = ref([])
const activities = ref([])
const testUserId = ref('')
const testActivityId = ref('')
const loading = ref(false)

const loadRiskRecords = async () => {
  try {
    const res = await axios.get('/api/coupon/risk-records')
    if (res.data.success) {
      riskRecords.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载风控记录失败')
  }
}

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

const triggerRiskTest = async () => {
  try {
    await ElMessageBox.confirm(
      '将模拟1分钟内连续领取5次优惠券，触发风控拦截。确定开始测试？',
      '风控测试确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    loading.value = true
    
    let successCount = 0
    let blocked = false
    let blockMessage = ''

    for (let i = 0; i < 5; i++) {
      try {
        const res = await axios.post('/api/coupon/receive', {
          userId: testUserId.value,
          activityId: testActivityId.value
        })
        if (res.data.success) {
          successCount++
          ElMessage.success(`第${i + 1}次领取成功`)
        } else {
          blocked = true
          blockMessage = res.data.message
          ElMessage.error(`第${i + 1}次领取被拦截: ${res.data.message}`)
          break
        }
      } catch (e) {
        blocked = true
        blockMessage = e.response?.data?.message || e.message
        ElMessage.error(`第${i + 1}次领取被拦截`)
        break
      }
      await new Promise(resolve => setTimeout(resolve, 300))
    }

    loading.value = false

    if (blocked) {
      ElMessageBox.alert(
        `✅ 风控拦截生效！拦截原因：${blockMessage}\n\n请刷新列表查看风控记录`,
        '风控测试完成',
        {
          confirmButtonText: '确定',
          type: 'success'
        }
      )
    } else {
      ElMessage.success('5次领取全部成功（未触发风控，可能用户已达领取限制')
    }

    loadRiskRecords()
  } catch (e) {
    loading.value = false
    if (e !== 'cancel') {
      ElMessage.error('测试失败: ' + e.message)
    }
  }
}

onMounted(() => {
  loadRiskRecords()
  loadUsers()
  loadActivities()
})
</script>
