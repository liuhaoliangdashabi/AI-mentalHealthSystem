<template>
    <div>
        <PageHead title="知识文章">
            <template #buttons>
                <el-button type="primary">新增</el-button>
            </template>
        </PageHead>
        <TableSearch :formItem="formItem" @search="handleSearch"/>
        <el-table :data="tableData" style="width:100%;margin-top:25px">
            <el-table-column prop="title" label="文章标题" width="200">
                <template #default="scope.row">
                    <div style="display:flex;align-items:center">
                        <el-icon><timer/></el-icon>
                        <span>{{ scope.row.title }}</span>
                    </div>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script setup>
import {ref,reactive,onMounted} from 'vue'
import PageHead from '@/components/PageHead.vue'
import TableSearch from '@/components/TableSearch.vue';
import {categoryTree,articlePage} from '@/api/admin'

const formItem=[
    {comp:'input',prop:'title',label:'标题',placeholder:'请输入文章标题'},
    {comp:'select',prop:'categoryId',label:'分类',placeholder:'请选择分类'},
    {comp:'select',prop:'status',label:'状态',placeholder:'请输入文章内容',options:[
        {label:"草稿",value:0},
        {label:"已发布",value:1},
        {label:"已下线",value:2}
    ]}
]

const categoryMap=reactive([])
const categories=ref([])
const tableData=ref([])
onMounted(async()=>{
    const data=await categoryTree()
    console.log(data,'分类树')

    categories.value=data.map(item=>{
        categoryMap[item.id]=item.categoryName
        return {
            label:item.categoryName,
            value:item.id
        }
    })
    formItem[1].options=categories.value
    handleSearch()
})
//分页参数
const pagination=reactive({
    currentPage:1,
    size:10,
    total:0
})


const handleSearch=async (formData={})=>{
    console.log("查询参数",formData)
    const params={
        ...pagination,
        ...formData
    }
    const {records,total}= await articlePage(params)
    tableData.value=records
}
</script>