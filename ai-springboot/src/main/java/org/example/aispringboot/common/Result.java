package org.example.aispringboot.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private String code;
    private T data;
    private String msg;

    public static <T> Result<T> success(){
        Result<T> result=new Result<>();
        result.setCode("200");
        result.setMsg("success");
        return result;
    }

    public static <T> Result<T> success(T data){
        Result<T> result=success();
        result.setData(data);
        return result;
    }
}
