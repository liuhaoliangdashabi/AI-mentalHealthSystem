package org.example.aispringboot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix="jwt")
public class JwtConfig {
    private String secret;
    private Long expiration;
    private Long refreshExpiration;//Integer装毫秒有溢出风险
    private String header;
    private String tokenPrefix;
}
