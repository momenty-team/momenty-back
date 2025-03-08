package com.momenty.global.auth.oauth.apple.dto;

import static com.momenty.user.exception.UserExceptionMessage.AUTHENTICATION;

import com.momenty.global.exception.GlobalException;
import java.util.List;

public record ApplePublicKeyResponse(List<ApplePublicKey> keys) {

    public ApplePublicKey getMatchedKey(String kid, String alg) throws GlobalException {
        return keys.stream()
                .filter(key -> key.kid().equals(kid) && key.alg().equals(alg))
                .findAny()
                .orElseThrow(() -> new GlobalException(AUTHENTICATION.getMessage(), AUTHENTICATION.getStatus()));
    }
}