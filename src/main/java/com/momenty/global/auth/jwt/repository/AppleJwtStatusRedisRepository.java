package com.momenty.global.auth.jwt.repository;

import com.momenty.global.auth.jwt.domain.AppleJwtStatus;
import org.springframework.data.repository.Repository;

public interface AppleJwtStatusRedisRepository extends Repository<AppleJwtStatus, String> {

    void save(AppleJwtStatus jwtStatus);
}
