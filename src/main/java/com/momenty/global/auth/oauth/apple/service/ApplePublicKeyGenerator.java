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

        return getPublicKey(publicKey);
    }

    private PublicKey getPublicKey(ApplePublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] nBytes = Base64.getUrlDecoder().decode(publicKey.n()); // 모듈러스(n) 디코딩
        byte[] eBytes = Base64.getUrlDecoder().decode(publicKey.e()); // 지수(e) 디코딩

        // RSA 공개 키 스펙 생성
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(1, nBytes),
                new BigInteger(1, eBytes));

        // Java에서 사용할 수 있는 PublicKey 객체 생성
        KeyFactory keyFactory = KeyFactory.getInstance(publicKey.kty());
        return keyFactory.generatePublic(publicKeySpec);
    }
}
