<template>
    <div>
        <PageHead title="咨询记录"/>
        <el-table :data="tableData" style="width:100%">
            <el-table-column label="会话ID" width="150">
                <template #default="scope">
                    <el-avatar>{{ scope.row.userNickname }}</el-avatar>
                </template>
            </el-table-column>
            <el-table-column labe="情绪日志">
                <template #default="scope">
                    <div class="session-title">{{ scope.row.sessionTitle }}</div>
                    <div class="session-preview">{{ scope.row.lastMessageContent }}</div>
                </template>
            </el-table-column>
            <el-table-column prop="messageCount" label="消息数" width="100"/>
            <el-table-column prop="lastMessageTime" label="实践" width="100"/>
            <el-table-column labe="操作" width="100">
                <template #default="scope">
                    <el-button type="primary" text @click="viewSessionDetail(scope.row)">详情</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
            style="margin-top:15px"
            :page-size="pagination.size"
            layout="prev,pager,next"
            :total="pagination.total"
            @change="handleChange"/>
        <el-dialog
            v-model="showDetailDialog"
            title="咨询会话详情"
            width="70%"
            :close-on-click-modal="false"
        >
            <div class="session-detail">
                <div class="detail-header">
                    <div class="detail-row">
                        <div class="detail-label">用户:</div>
                        <div class="detail-value">{{ sessionDetail.userNickname }}</div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">开始时间:</div>
                        <div class="detail-value">{{ sessionDetail.startedAt }}</div>
                    </div>
                    <div class="detail-row">
                        <div class="detail-label">消息数量:</div>
                        <div class="detail-value">{{ sessionDetail.messageCount}}</div>
                    </div>
                </div>
                <div class="session-container"></div>
            </div>
        </el-dialog>
    </div>
</template>

<script setup>
import PageHead from '@/components/PageHead.vue'
import { onMounted,ref,reactive } from 'vue';
import {getConsultationPage,getSessionDetail} from '@/api/admin'
const tableData=ref([])
const pagination=reactive({
    currentPage:1,
    size:10,
    total:0
})
const showDetailDialog=ref(false)
const sessionDetail=ref({})
const sessionMessages=ref([])


const viewSessionDetail=(row)=>{
    getSessionDetail(row.sessionId).then(res=>{
        sessionMessages.value=res
        showDetailDialog.value=true
        viewSessionDetail.value=row
    })
   
}

const handleChange=(page)=>{
    pagination.currentPage=page
    handleSearch()
}
const handleSearch=()=>{
    getConsultationPage(pagination).then(res=>{
        const {records,total}=res
        tableData.value=records
        pagination.total=total
    })
}
onMounted(()=>{
    handleSearch()
})
</script>
