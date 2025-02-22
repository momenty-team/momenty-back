package com.momenty.user.service;

import static com.momenty.util.mail.Title.*;

import com.momenty.user.domain.User;
import com.momenty.user.domain.UserTemporaryStatus;
import com.momenty.user.dto.request.UserAuthenticationRequest;
import com.momenty.user.dto.request.UserRegisterRequest;
import com.momenty.user.dto.response.UserRegisterResponse;
import com.momenty.user.repository.UserRedisRepository;
import com.momenty.user.repository.UserRepository;
import com.momenty.util.mail.MailService;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private static final int CODE_LENGTH = 6;

    private final UserRedisRepository userRedisRepository;
    private final MailService mailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void register(
            @Valid UserRegisterRequest userRegisterRequest
    ) {
        String email = userRegisterRequest.email();
        String authenticationNumber = createCode(CODE_LENGTH);

        UserTemporaryStatus userTemporaryStatus = UserTemporaryStatus.of(userRegisterRequest, authenticationNumber);
        userRedisRepository.save(userTemporaryStatus);

        mailService.sendEmail(email, REGISTER_TITLE.getContent(), authenticationNumber);
    }

    @Transactional
    public void register(
            User userInfo
    ) {
        userRepository.save(userInfo);
    }

    private String createCode(
            int length
    ) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }

    // TODO: 에러 바꾸기
    @Transactional
    public void authenticate(@Valid UserAuthenticationRequest authenticationRequest) {
        UserTemporaryStatus userTemporaryStatus = userRedisRepository.getById(authenticationRequest.email());
        if (!authenticationRequest.authenticationNumber().equals(userTemporaryStatus.getAuthenticationNumber())) {
            throw new RuntimeException();
        }

        userRedisRepository.deleteById(authenticationRequest.email());
        userRepository.save(userTemporaryStatus.toUser(passwordEncoder));
    }
}
