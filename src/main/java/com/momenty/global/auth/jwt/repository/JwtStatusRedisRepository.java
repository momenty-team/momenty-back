package com.momenty.global.auth.jwt.repository;

import com.momenty.global.auth.jwt.domain.JwtStatus;
import org.springframework.data.repository.Repository;

public interface JwtStatusRedisRepository extends Repository<JwtStatus, String> {

    void save(JwtStatus jwtStatus);
}
