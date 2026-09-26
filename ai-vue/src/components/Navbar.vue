<template>
    <div class="navbar">
        <div class="flex-box">
            <el-button @click="handleCollapse">
                <el-icon><Expand/></el-icon>
            </el-button>
            <p class="page-title">{{route.meta.title}}</p>
        </div>
        <div class="flex-box">
            <el-dropdown @command="handleCommand">
                <div class="flex-box">
                    <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"/>
                    <el-icon><ArrowDown/></el-icon>
                </div>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script setup>
import {ref} from 'vue'
import { logout } from '@/api/admin'
import { useAdminStore } from '@/stores/admin'
import { useRoute,useRouter } from 'vue-router'
import { ElMessage,ElMessageBox } from 'element-plus'
const route=useRoute()
const router=useRouter()
const handleCommand=(command)=>{
    console.log(command)
    if(command==='logout'){
        ElMessageBox.confirm('确定退出登录吗？','提示',{
            confirmButtonText:'确定',
            cancelButtonText:'取消',
            type:'warning'
        }).then(()=>{
            logout().then(()=>{
                localStorage.removeItem('token')
                localStorage.removeItem('userInfo')
                ElMessage.success('成功退出登录');
                router.push('/auth/login')
            })
        }).catch(()=>{
            ElMessage.info('已取消退出登录')
        })
    }
}

const handleCollapse=()=>{
    useAdminStore().toggleCollapse()
}
</script>

<style lang="scss" scoped>
.navbar{
    height:100%;
    display:flex;
    align-items:center;
    justify-content:space-between;
    padding:0 25px;
    background:white;
    box-shadow:0 1px 4px rgba(0,21,41,0.08);
    border-bottom:1px solid #e5e7eb;
    .flex-box{
        display:flex;
        align-items:center;
        justify-content:center;
    }
    .page-title{
        margin-left:20px;
        font-size:26px;
        font-weight:bold;
        color:#1f2937;
    }
}

</style>