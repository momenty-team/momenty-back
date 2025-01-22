package com.momenty.user.service;

import com.momenty.user.dto.request.UserRegisterRequest;
import com.momenty.user.dto.response.UserRegisterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    @Transactional
    public UserRegisterResponse register(
            @Valid UserRegisterRequest userRegisterRequest
    ) {
        String email = userRegisterRequest.email();
        /*
        TODO
         1. 인증번호 생성 후 레디스에 유저 정보와 함께 저장
         2. 인증번호를 메일로 전송
         3. 인증번호가 맞으면 레디스에서 제거 후 db에 저장 (다른 api)
        */
        return null;
    }
}
