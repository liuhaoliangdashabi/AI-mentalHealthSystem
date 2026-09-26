package org.example.aispringboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    //白名单
    private static final String[] PUBLIC_PATHS={
            "/",
            "/api/user/login"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //禁用CSRF保护（API服务一般不需要）
                .csrf(AbstractHttpConfigurer::disable)
                //配置会话管理无状态——后续用JWT
                .sessionManagement(session->{
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                //配置请求的授权规则
                .authorizeHttpRequests(auth->auth
                        //公开路径，无需登录即可访问
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        //其他都要认证
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
