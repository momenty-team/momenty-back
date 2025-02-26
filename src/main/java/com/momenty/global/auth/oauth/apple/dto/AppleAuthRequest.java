package com.momenty.global.auth.oauth.apple.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppleAuthRequest {
    private String state;
    private String code;
    private String id_token;
}

