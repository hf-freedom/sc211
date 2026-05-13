<template>
  <div>
    <el-button type="primary" @click="showCreateDialog = true" style="margin-bottom: 20px">
      添加用户
    </el-button>

    <el-table :data="users" border stripe>
      <el-table-column prop="id" label="用户ID" width="250" />
      <el-table-column prop="name" label="用户名" width="200" />
      <el-table-column prop="level" label="等级" width="100">
        <template #default="{ row }">
          <el-tag type="primary">LV{{ row.level }}</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showCreateDialog" title="添加用户" width="400px">
      <el-form :model="newUser" label-width="100px">
        <el-form-item label="用户ID">
          <el-input v-model="newUser.id" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="newUser.name" />
        </el-form-item>
        <el-form-item label="等级">
          <el-input-number v-model="newUser.level" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="addUser">添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const users = ref([])
const showCreateDialog = ref(false)
const newUser = ref({
  id: '',
  name: '',
  level: 1
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

const addUser = async () => {
  try {
    const res = await axios.post('/api/coupon/user', newUser.value)
    if (res.data.success) {
      ElMessage.success('添加成功')
      showCreateDialog.value = false
      loadUsers()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

onMounted(() => {
  loadUsers()
})
</script>
