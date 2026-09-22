import service from "../utils/request";
export const register=(data)=>{
    return service.post('/user/add',data)
}