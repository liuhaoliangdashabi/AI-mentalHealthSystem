<template>
    <el-dialog
        title="文章详情"
        v-model="dialogVisible"
        width="50%"
        @close="handleClose"
    >
        <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
            <el-form-item label="文章标题" prop="title">
                <el-input v-model="formData.title" placeholder="请输入文章标题"
                    maxlength="200" show-word-limit clearable/>
            </el-form-item>
            <el-form-item label="所属分类" prop="categoryId">
                <el-select v-model="formData.categoryId" placeholder="请选择分类">
                    <el-option v-for="item in props.categories" :key="item.value"
                        :label="item.label" :value="item.value"/>
                </el-select>
            </el-form-item>
        </el-form>
    </el-dialog>
</template>

<script setup>
import {ref,reactive,computed,onMounted} from 'vue'
const props=defineProps({
    modelValue:{
        type:Boolean,
        default:false
    },
    categories:{
        type:Array,
        default:()=>[]
    }
})
const emit=defineEmits(['update:modelValue'])
const dialogVisible=computed({
    get(){
        return props.modelValue
    },
    set(val){
        emit('update:modelValue',val)
    }
})

const formData=reactive({
    title:"",
    content:"",
    coverImage:"",
    categoryId:1,
    summary:"",
    tags:"",
    id:""
})
const rules=reactive({
    title:[
        {required:true,message:'请输入文章标题',trigger:'blur'}
    ],
})
const handleClose=()=>{

}
</script>