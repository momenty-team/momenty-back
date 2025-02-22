package com.momenty.global.auth.oauth.util;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jose.crypto.ECDSASigner;
import java.security.interfaces.ECPrivateKey;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ClientSecret {

    public static String createClientSecret(String teamId, String clientId, String keyId, String keyPath, String iss) {
        try {
            long now = System.currentTimeMillis() / 1000;
            Date issueTime = new Date(now * 1000);
            Date expirationTime = new Date((now + 15777000) * 1000); // 6개월 유효

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                    .keyID(keyId)
                    .build();

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer(teamId)
                    .subject(clientId)
                    .audience(iss)
                    .issueTime(issueTime)
                    .expirationTime(expirationTime)
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            PrivateKey privateKey = loadPrivateKey(keyPath);
            JWSSigner signer = new ECDSASigner((ECPrivateKey) privateKey);
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create client secret", e);
        }
    }

    private static PrivateKey loadPrivateKey(String keyPath) throws Exception {
        InputStream inputStream = ClientSecret.class.getClassLoader().getResourceAsStream(keyPath);
        if (inputStream == null) {
            throw new FileNotFoundException("Private key file not found: " + keyPath);
        }

        byte[] keyBytes = inputStream.readAllBytes();
        String keyString = new String(keyBytes, StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(spec);
    }

}

