<template>
   <el-aside width="264px">
     <el-menu
        default-active="2"
        class="menu-style"
      >
        <div class="brand">
            <el-image style="width:40px;height:40px;" :src="iconUrl" alt="logo"/>
            <div class="info-card">
                <h1 class="brand-title">心理健康AI助手</h1>
                <p class="brand-subtitle">管理后台</p>
            </div>
        </div>
        <el-menu-item @click="selectMenu" v-for="item in router.options.routes[0].children" :key="item.path" :index="item.path">
          <el-icon><component :is="item.meta.icon"/></el-icon>
          <span>{{ item.meta.title }}</span>
        </el-menu-item>
      </el-menu>
   </el-aside>
</template>


<style lang="scss" scoped>
.menu-style{
    height:100%;
    .brand{
        display:flex;
        align-items:center;
        justify-content:center;
        padding:10px;
        background-color:#fff;
        border-bottom:1px solid #e5e7eb;
        margin-left:-40px;

        .brand-title{
            font-size:15px;
            font-weight:bold;
            margin-bottom:5px;
            color:#1f2937;
            margin-left:20px;
        }
        .brand-subtitle{
            margin-left:20px;
            font-size:12px;
            color:#6b7280;
        }
    }
}

</style>


<script setup>
import {useRouter} from 'vue-router'
const router=useRouter()

const iconUrl=new URL('@/assets/机器人.png',import.meta.url).href
const selectMenu=(key)=>{
    console.log(key)
    const currentRoute=router.options.routes[0]
    `${currentRoute.path}/${key.index}`
}
</script>