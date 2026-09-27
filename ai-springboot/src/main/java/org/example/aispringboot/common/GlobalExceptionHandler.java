package org.example.aispringboot.common;

import lombok.extern.slf4j.Slf4j;
import org.example.aispringboot.exception.BusinessException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handlerException(MethodArgumentNotValidException e){
        log.warn("掉进了参数校验异常捕获，多半是参数不符合格式,{}",e);
        String message=e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return Result.error(ResultCode.PARAM_ERROR.getCode(), ResultCode.PARAM_ERROR.getMsg(),message);
    }

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e){
        log.warn("掉进了business异常捕获，多半是业务有问题,{}",e);
        //如果异常携带额外的data
        if(e.getData()!=null){
            return Result.error(e.getCode(),e.getMessage(),e.getData());
        }else{
            return Result.error(e.getCode(),e.getMessage(),null);
        }
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e){
        log.error("系统异常", e);                    // error + 必须带 e
        return Result.error(ResultCode.SYSTEM_ERROR.getCode(), "系统繁忙，请联系管理员", null);
    }
}
