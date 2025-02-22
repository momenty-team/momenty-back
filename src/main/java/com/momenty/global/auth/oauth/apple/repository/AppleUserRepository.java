package com.momenty.global.auth.oauth.apple.repository;

import com.momenty.global.auth.oauth.apple.domain.AppleUser;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface AppleUserRepository extends Repository<AppleUser, Integer> {

    Optional<AppleUser> findBySub(String sub);

    void save(AppleUser appleUser);
}
