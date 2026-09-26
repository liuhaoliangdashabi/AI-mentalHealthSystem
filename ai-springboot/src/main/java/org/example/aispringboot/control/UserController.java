package org.example.aispringboot.control;

import jakarta.validation.Valid;
import org.example.aispringboot.DTO.command.UserLoginCommandDTO;
import org.example.aispringboot.DTO.response.UserLoginResponseDTO;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.entity.User;
import org.example.aispringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginCommandDTO commandDTO){
        Result<UserLoginResponseDTO> result=userService.login(commandDTO);
        System.out.println(result);
        return result;
    }
}
