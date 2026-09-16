import service from "../utils/request";
export function login(data){
    return service.post('/user/login',data)
}
export function categoryTree(){
    return service.get('/knowledge/category/tree')
}
export function articlePage(params){
    return service.get('/knowledge/article/page',{params:params})
}
export function uploadFile(file,businessInfo){
    const formData=new FormData()
    formData.append('file',file)
    formData.append('businessType','ARTICLE')
    formData.append('businessId',businessInfo.businessId)
    formData.append('businessField','cover')

    return service.post('file/upload',formData,{
        headers:{
            'content-type':'multipart/form-data'
        }
    })
}

export function createArticle(data){
    return service.post('/knowledge/article',data)
}

export function getArticleDetail(id){
    return service.get(`/knowledge/article/${id}`)
}