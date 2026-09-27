package org.example.aispringboot.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.example.aispringboot.config.JwtConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Date;
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
            throw new RuntimeException("生成token失败，原因：",e);
        }
    }
}
