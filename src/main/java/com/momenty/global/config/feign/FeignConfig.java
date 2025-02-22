package com.momenty.global.config.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.momenty.global.auth.oauth.apple.controller")
public class FeignConfig {

}
