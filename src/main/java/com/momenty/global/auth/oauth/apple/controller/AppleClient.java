package com.momenty.global.auth.oauth.apple.controller;

import com.momenty.global.auth.oauth.apple.dto.ApplePublicKeyResponse;
import com.momenty.global.auth.oauth.apple.dto.AppleTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "uniqueAppleClient", url = "https://appleid.apple.com")
public interface AppleClient {

    @GetMapping("/auth/keys")
    ApplePublicKeyResponse getAppleAuthPublicKey();

    @PostMapping(value = "/auth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    AppleTokenResponse getAppleToken(
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri,
            @RequestParam("code") String code,
            @RequestParam("client_secret") String clientSecret,
            @RequestParam("grant_type") String grantType
    );
}
