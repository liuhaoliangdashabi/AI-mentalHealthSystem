package org.example.aispringboot.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.aispringboot.config.JwtConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
@Slf4j
@Component
public class JwtTokenUtil implements ApplicationContextAware {
    private static final String ISSUER="mental-health-assistant";

    private static ApplicationContext applicationContext;

    //静态工具类中获取Spring容器管理的Bean
    @Override
    public void setApplicationContext(ApplicationContext applicationContext){
        JwtTokenUtil.applicationContext=applicationContext;
    }


    private static JwtConfig getJwtConfig(){
        return applicationContext.getBean(JwtConfig.class);
    }

    //生成token
    public static String generateToken(Long userId,String username,Integer roleType){
        try {
            log.debug("现在正在生成token");
            //获取配置项（Autowired/注入到上下文()）
            JwtConfig jwtConfig=getJwtConfig();
            //生成签名算法——调用HMAC256
            Algorithm algorithm=Algorithm.HMAC256(jwtConfig.getSecret());
            //生成过期时间
            Date expiration=new Date(System.currentTimeMillis()+jwtConfig.getExpiration());

            String token=JWT.create()
                    .withClaim("userId",userId)
                    .withClaim("username",username)
                    .withClaim("roleType",roleType)
                    .withIssuedAt(new Date())
                    .withIssuer(ISSUER)
                    .withExpiresAt(expiration)
                    .sign(algorithm);
            return token;
        } catch (Exception e) {
            log.info("生成token失败，错误原因：{}",e);
            throw new RuntimeException("生成token失败，原因：",e);
        }
    }

    //提取token
    public static String extractTokenFromRequest(HttpServletRequest request){
        if(request==null){
            return null;
        }
        String tokenHeader=request.getHeader("token");
        if(StringUtils.hasText(tokenHeader)){
            return tokenHeader;
        }
        return null;

    }

    //验证token,并提取荷载部分
    @Getter
    public static class TokenVeriticationResult{
        private final Long userId;
        private final String username;
        private final Integer roleType;
        private final boolean valid;
        public TokenVeriticationResult(Long userId, String username, Integer roleType, boolean valid) {
            this.userId = userId;
            this.username = username;
            this.roleType = roleType;
            this.valid = valid;
        }
    }
    public static TokenVeriticationResult validateToken(String token){
        DecodedJWT jwt=verifyToken(token);
        Long userId=jwt.getClaim("userId").asLong();
        String username=jwt.getClaim("username").asString();
        //角色类型需要兼容——可能传字符，也可能传数字
        Integer roleType=null;
        try{
            roleType=jwt.getClaim("roleType").asInt();
        }catch(Exception e){
            String roleTypeStr=jwt.getClaim("roleType").asString();
            if(StringUtils.hasText(roleTypeStr)){
                roleType=Integer.valueOf(roleTypeStr);
            }
        }

        if(userId!=null && StringUtils.hasText(username) && roleType!=null){
            return new TokenVeriticationResult(userId,username,roleType,true);
        }
        return null;
    }

    //验证token是否有效
    public static DecodedJWT verifyToken(String token){
        if(!StringUtils.hasText(token)){
            log.info("验证token无效——token为空,token={}",token);
            throw new JWTVerificationException("Token不能为空");
        }
        //token解码
        JwtConfig jwtConfig=getJwtConfig();
        Algorithm algorithm=Algorithm.HMAC256(jwtConfig.getSecret());
        JWTVerifier verifyToken = JWT.require(algorithm).withIssuer(ISSUER).build();
        return verifyToken.verify(token);
    }
}
