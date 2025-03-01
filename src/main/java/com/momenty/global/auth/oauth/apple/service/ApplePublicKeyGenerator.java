package com.momenty.global.auth.oauth.apple.service;

import com.momenty.global.auth.oauth.apple.dto.ApplePublicKey;
import com.momenty.global.auth.oauth.apple.dto.ApplePublicKeyResponse;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplePublicKeyGenerator {
    public PublicKey generatePublicKey(
            Map<String, String> tokenHeaders,
            ApplePublicKeyResponse applePublicKeys
    ) throws NoSuchAlgorithmException, InvalidKeySpecException {
        ApplePublicKey publicKey = applePublicKeys.getMatchedKey(tokenHeaders.get("kid"),
                tokenHeaders.get("alg"));

        if (publicKey == null) {
            throw new IllegalArgumentException("No matching Apple public key found for the given 'kid' and 'alg'.");
        }

        return getPublicKey(publicKey);
    }

    private PublicKey getPublicKey(ApplePublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] nBytes = base64UrlDecode(publicKey.n()); // 모듈러스(n) 디코딩
        byte[] eBytes = base64UrlDecode(publicKey.e());; // 지수(e) 디코딩

        if (nBytes.length == 0 || eBytes.length == 0) {
            throw new IllegalArgumentException("Invalid Apple public key data: 'n' or 'e' is empty.");
        }

        // RSA 공개 키 스펙 생성
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(1, nBytes),
                new BigInteger(1, eBytes));

        // Java에서 사용할 수 있는 PublicKey 객체 생성
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicKeySpec);
    }

    private byte[] base64UrlDecode(String value) {
        int paddingLength = (4 - (value.length() % 4)) % 4; // 패딩 길이 계산
        String paddedValue = value + "=".repeat(paddingLength); // 패딩 추가
        return Base64.getUrlDecoder().decode(paddedValue);
    }
}
