<template>
    <el-form :model="formData">
        <template v-for="item in formItem" :key="item.prop">
            <el-form-item :label="item.label" :prop="itme.prop">
                <component v-model="formData[item.prop]" :is="isComp(item.comp)" :placeholder="item.placeholder">
                    <template v-if="item.comp==='select'">
                        <el-option label="全部" value=""></el-option>
                        <el-option 
                            v-for="opt in item.options"
                            :key="opt.value"
                            :label="opt.label"
                            :value="opt.value"/>
                    </template>
                </component>
            </el-form-item>
        </template>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button type="info" @click="handleReset">重置</el-button>
    </el-form>
</template>

<script setup>
import {ref,reactive} from 'vue'

//表单数据
const formData=reactive({})



const props=defineProps({
    formItem:{
        type:Array,
        default:()=>[]
    }
})
const isComp=(comp)=>{
    return {
        input:'elInput',
        select:'elSelect'
    }[comp]
}
const handleSearch=()=>{

}
const handleReset=()=>{

}
</script>