import axios from 'axios'
import {ElMessage} from 'element-plus'

const service=axios.create({
    baseURL:'/api',
    timeout:5000
})
service.interceptors.request.use(
    (config)=>{
        const token=localStorage.getItem('token')
        if(token){
            config.headers['token']=token
        }
        return config
    },
    (error)=>{
        return Promise.reject(error)
    }
)
service.interceptors.response.use(
    (response)=>{
        const {data,config} = response
        if(data.code==='200'){
            return data.data
        }else if(data.code==='401'){
            // 401：未登录 / token 过期，统一清缓存跳登录页
            ElMessage.error(data.msg||'登录过期，请重新登录')
            localStorage.removeItem('token')
            localStorage.removeItem('UserInfo')
            window.location.href='/auth/login'
            return Promise.reject(data)
        }else{
            // 业务错误（-1 操作失败 / 400 参数错误 / BUSINESS_ERROR 等）：只弹窗不跳转
            // 参数校验失败时后端会把具体字段错误放在 data 里（字符串），优先展示
            const detail=typeof data.data==='string'?data.data:''
            ElMessage.error(detail||data.message||data.msg||'请求失败')
            return Promise.reject(data)
        }
    },
    (error)=>{
        console.log('响应错误',error)
        return Promise.reject(error)
    }
)

export default service