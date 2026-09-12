<template>
    <div class="backend-layout">
    <el-container class="main-container">
      <SideBar/>
      <el-container>
        <Navbar/>
        <router-view/>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import SideBar from './SideBar.vue'
import Navbar from './Navbar.vue'
</script>

<style lang="scss" scoped>
.backend-layout{
    height:100vh;
    .main-container{
        height:100%;
    }
}

</style>