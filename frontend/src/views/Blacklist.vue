<template>
  <div>
    <el-button type="primary" @click="showAddDialog = true" style="margin-bottom: 20px">
      添加黑名单
    </el-button>

    <el-table :data="blacklist" border stripe>
      <el-table-column prop="userId" label="用户ID" width="250" />
      <el-table-column prop="userName" label="用户名" width="200" />
      <el-table-column prop="reason" label="原因" width="200" />
      <el-table-column prop="createdAt" label="创建时间" width="180" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="danger" size="small" @click="removeFromBlacklist(row.userId)">
            移除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showAddDialog" title="添加黑名单" width="500px">
      <el-form :model="newBlacklist" label-width="100px">
        <el-form-item label="用户ID">
          <el-input v-model="newBlacklist.userId" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="newBlacklist.userName" />
        </el-form-item>
        <el-form-item label="原因">
          <el-input v-model="newBlacklist.reason" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addToBlacklist">添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const blacklist = ref([])
const showAddDialog = ref(false)
const newBlacklist = ref({
  userId: '',
  userName: '',
  reason: ''
})

const loadBlacklist = async () => {
  try {
    const res = await axios.get('/api/coupon/blacklist')
    if (res.data.success) {
      blacklist.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载黑名单失败')
  }
}

const addToBlacklist = async () => {
  try {
    const res = await axios.post('/api/coupon/blacklist', newBlacklist.value)
    if (res.data.success) {
      ElMessage.success('添加成功')
      showAddDialog.value = false
      loadBlacklist()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

const removeFromBlacklist = async (userId) => {
  try {
    const res = await axios.delete(`/api/coupon/blacklist/${userId}`)
    if (res.data.success) {
      ElMessage.success('移除成功')
      loadBlacklist()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error('移除失败')
  }
}

onMounted(() => {
  loadBlacklist()
})
</script>
