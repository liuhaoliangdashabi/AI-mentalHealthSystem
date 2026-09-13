import {ref} from 'vue'
import {defineStore} from 'pinia'
export const useAdminStore=defineStore('admin',()=>{
    const isCollapse=ref(false)
    const toggleCollapse=()=>{
        isCollapse.value=!isCollapse.value
    }
    return{
        isCollapse,
        toggleCollapse
    }
})