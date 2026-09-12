<script setup>
import { ElMessage } from 'element-plus'
import BackendLayout from './components/BackendLayout.vue'

const handleClick = () => {
  ElMessage.success('Element Plus 引入成功')
}
</script>

<template>
  <router-view/>
</template>
