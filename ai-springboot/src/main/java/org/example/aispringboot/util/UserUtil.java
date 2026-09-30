package org.example.aispringboot.util;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.example.aispringboot.config.JwtConfig;

@Slf4j
public class UserUtil {
    private final JwtTokenUtil jwtTokenUtil;

    public JwtTokenUtil(JwtTokenUtil jwtTokenUtil){
        this.jwtTokenUtil=jwtTokenUtil;
    }

    public static Long getCurrentUserId(){
        //获取当前用户
        String token=jwtTokenUtil.getCurrentToken();
        DecodedJWT jwt= jwtTokenUtil.verifyToken(token);
        return jwt.getClaim("userId").asLong();
    }
}
