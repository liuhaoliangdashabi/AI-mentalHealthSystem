package org.example.aispringboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.example.aispringboot.DTO.command.UserLoginCommandDTO;
import org.example.aispringboot.DTO.response.UserLoginResponseDTO;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.entity.User;
import org.example.aispringboot.exception.BusinessException;
import org.example.aispringboot.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    public Result<UserLoginResponseDTO> login(UserLoginCommandDTO commandDTO){
        //构建查询条件
        LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,commandDTO.getUsername())
                .or()
                .eq(User::getEmail,commandDTO.getUsername());

        User user=userMapper.selectOne(queryWrapper);
        System.out.println(user);

        if(user==null){
            throw new BusinessException("查找的用户不存在");
        }else{
            String inputPassword=commandDTO.getPassword().trim();
            if(!passwordEncoder.matches(inputPassword,user.getPassword())){
                throw new BusinessException("密码错误");
            }
            if(!user.isActive()){
                throw new BusinessException("用户已被禁用，请联系管理员");
            }

        }
        return null;
    }
}
