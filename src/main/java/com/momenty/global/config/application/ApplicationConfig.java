package com.momenty.global.config.application;


import com.momenty.global.auth.oauth.apple.domain.AppleAuthProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AppleAuthProperty.class)
public class ApplicationConfig {

}
