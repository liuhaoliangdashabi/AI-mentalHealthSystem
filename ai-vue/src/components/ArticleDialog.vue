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
            <el-form-item label="文章摘要" prop="summary">
                <el-input type="textarea" v-model="formData.summary" placeholder="请输入文章摘要"
                    maxlength="1000" show-word-limit :rows="4"/>
            </el-form-item>
            <el-form-item label="标签" prop="tags">
                <el-select v-model="formData.tagArray" placeholder="请输入文章标签(逗号分隔)"
                    multiple filterable allow-create style="width:100%"
                    >
                    <el-option v-for="tag in commonTags" :key="tag" :label="tag" :value="tag"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="封面图片"><!--prop不需要加，因为上传部分自定义，没必要和原数据做关联-->
                <div class="cover-upload">
                    <el-upload
                        class="avatar-uploader"
                        action="#"
                        :before-upload="beforeUpload"
                        :http-request="handleUploadRequest"
                        :show-file-list="false"
                        accept="image/*"
                        >
                            <div v-if="!imgUrl" class="cover-placeholder">
                                <p>点击上传封面</p>
                            </div>
                            <img v-else style="width:160px;" :src="imgUrl" class="cover-image" alt="封面图片">
                    </el-upload>
                    <div v-if="imgUrl" class="cover-remove">
                        <el-button type="danger" size="small" @click="handleRemove">移除封面</el-button>
                    </div>
                </div>
            </el-form-item>
        </el-form>
    </el-dialog>
</template>

<script setup>
import {ref,reactive,computed,onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {uploadFile} from '@/api/admin'
import {fileBaseUrl} from '@/config/index.js'
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
        {required:true,message:'请输入文章标题',trigger:'blur'},
        {max:200,message:'文章标题最多200个字符',trigger:'blur'}
    ],
    categoryId:[
        {required:true,message:'请选择分类',trigger:'change'}
    ]
})
const handleClose=()=>{

}
const commonTags = [
  '情绪管理', '焦虑', '抑郁', '压力', '睡眠', 
  '冥想', '正念', '放松', '心理健康', '自我成长',
  '人际关系', '工作压力', '学习方法', '生活技巧'
]
const imgUrl=ref('')
const beforeUpload=(file)=>{
    console.log(file)
    const isImage=file.type.startsWith('image/')
    if(!isImage){
        ElMessage.error('上传图片,请选择图片文件')
        return false
    }
    const isLt10M=file.size/1024/1024<10
    if(!isLt10M){
        ElMessage.error('上传图片大小不能超过10MB')
        return false
    }
    return true
}
const handleUploadRequest=async({file})=>{//解构回调参数中的file
    //UUID生成
    const businessId=crypto.randomUUID()
    const fileRes=await uploadFile(file,{
        businessId:businessId
    })
    console.log(fileRes)
    //拼接路径
    imgUrl.value=`${fileBaseUrl}${fileRes.filePath}`
    formData.coverImage=fileRes.filePath//后端要的只是一个相对路径
}
const handleRemove=()=>{
    imgUrl.value=''
    formData.coverImage=''
}
</script>

<style lang="scss" scoped>
.cover-placeholder{
    width:200px;
    height:120px;
    display:flex;
    flex-direction:column;
    align-items:center;
    justify-content:center;
    color:#8b949e;
    background:#f6f8fa;
}
</style>