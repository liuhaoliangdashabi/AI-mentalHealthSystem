package org.example.aispringboot.util;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.example.aispringboot.config.JwtConfig;

@Slf4j
public class UserUtil {

    public static Long getCurrentUserId(){
        //获取当前用户
        String token=JwtTokenUtil.getCurrentToken();
        if(token==null)return null;
        DecodedJWT jwt= JwtTokenUtil.verifyToken(token);
        return jwt.getClaim("userId").asLong();
    }
}
