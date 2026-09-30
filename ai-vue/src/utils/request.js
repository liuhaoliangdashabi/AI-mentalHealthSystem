import axios from 'axios'
import {ElMessage} from 'element-plus'

const service=axios.create({
    baseURL:'/api',
    timeout:5000
})

// 清缓存并跳登录页
const toLogin=(msg)=>{
    ElMessage.error(msg||'登录过期，请重新登录')
    localStorage.removeItem('token')
    localStorage.removeItem('UserInfo')
    window.location.href='/auth/login'
}

// 统一的业务响应处理
// 后端返回的都是 {code,msg,data} 结构，HTTP 200 和 HTTP 401/403/400 都会走到这里
const handleResult=(data)=>{
    if(data.code==='200'){
        return data.data
    }
    // 未登录 / token 无效 / token 过期 / 访问未授权 —— 结果都是重新登录
    if(data.code==='401'||data.code==='A0230'||data.code==='A0301'){
        toLogin(data.msg)
        return Promise.reject(data)
    }
    // 其余业务错误：只弹窗不跳转
    // 参数校验失败时后端会把具体字段错误放在 data 里（字符串），优先展示
    const detail=typeof data.data==='string'?data.data:''
    ElMessage.error(detail||data.message||data.msg||'请求失败')
    return Promise.reject(data)
}

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
        return handleResult(response.data)
    },
    (error)=>{
        const response=error.response

        // 1. 压根没收到响应：超时 / 断网 / 跨域
        if(!response){
            const isTimeout=error.code==='ECONNABORTED'||error.code==='ETIMEDOUT'
            ElMessage.error(isTimeout?'请求超时，请稍后重试':'网络异常，请检查网络连接')
            return Promise.reject(error)
        }

        // 2. 收到响应但不是 2xx
        //    JwtAuthticationFilter 和 ResponseUtil.WriteError 写回来的也是 {code,msg,data} 结构
        const data=response.data
        if(data&&typeof data==='object'&&data.code!==undefined){
            return handleResult(data)
        }

        // 3. 响应体是空的或者不是 Result 结构（比如 502 网关错误、纯 401 空响应）
        if(response.status===401){
            toLogin()
        }else{
            ElMessage.error('请求失败（HTTP '+response.status+'）')
        }
        return Promise.reject(error)
    }
)

export default service
