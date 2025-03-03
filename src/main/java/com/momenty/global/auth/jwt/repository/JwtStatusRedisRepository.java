package com.momenty.global.auth.jwt.repository;

import com.momenty.global.auth.jwt.domain.JwtStatus;
import com.momenty.global.exception.InvalidJwtTokenException;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface JwtStatusRedisRepository extends Repository<JwtStatus, Integer> {

    Optional<JwtStatus> findById(Integer userId);

    default JwtStatus getById(Integer userId) {
        return findById(userId).orElseThrow(InvalidJwtTokenException::new);
    }

    JwtStatus save(JwtStatus updatedStatus);
}
