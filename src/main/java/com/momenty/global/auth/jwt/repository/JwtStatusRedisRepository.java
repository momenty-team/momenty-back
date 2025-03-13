package com.momenty.global.auth.jwt.repository;

import static com.momenty.user.exception.UserExceptionMessage.*;

import com.momenty.global.auth.jwt.domain.JwtStatus;
import com.momenty.global.exception.GlobalException;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface JwtStatusRedisRepository extends Repository<JwtStatus, Integer> {

    Optional<JwtStatus> findById(Integer userId);

    default JwtStatus getById(Integer userId) {
        return findById(userId).orElseThrow(
                () -> new GlobalException(NOT_FOUND_USER.getMessage(), NOT_FOUND_USER.getStatus())
        );
    }

    JwtStatus save(JwtStatus updatedStatus);

    void deleteById(Integer id);
}
