package org.example.aispringboot.common;

import org.example.aispringboot.exception.BusinessException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handlerException(MethodArgumentNotValidException e){
        String message=e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return Result.error(ResultCode.PARAM_ERROR.getCode(), ResultCode.PARAM_ERROR.getMsg(),message);
    }

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e){
        //如果异常携带额外的data
        if(e.getData()!=null){
            return Result.error(e.getCode(),e.getMessage(),e.getData());
        }else{
            return Result.error(e.getCode(),e.getMessage(),null);
        }
    }
}
