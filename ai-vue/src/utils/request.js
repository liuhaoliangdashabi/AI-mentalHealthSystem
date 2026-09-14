import axios from 'axios'
import {ElMessage} from 'element-plus'
import router from '@/router'

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
        }else{
            if(data.code==='-1'){
                if(!config.url?.includes('/login')){
                    ElMessage.error(data.msg||'登陆过期，请重新登录')
                    localStorage.removeItem('token')
                    localStorage.removeItem('userInfo')
                    window.location.href='auth/login'
                }else{
                    ElMessage.error(data.msg||'登录过期，请重新登录')
                    return Promise.reject('网络请求失败')
                }
            }
        }
        return response
    },
    (error)=>{
        console.log('响应错误',error)
        return Promise.reject(error)
    }
)

export default service