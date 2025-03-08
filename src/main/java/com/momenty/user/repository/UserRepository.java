package com.momenty.user.repository;

import static com.momenty.user.exception.UserExceptionMessage.NOT_FOUND_USER;

import com.momenty.global.exception.GlobalException;
import com.momenty.user.domain.User;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface UserRepository extends Repository<User, Integer> {

    User save(User user);

    Optional<User> findById(Integer id);

    Optional<User> findByNickname(String nickname);

    default User getById(Integer id) {
        return findById(id).orElseThrow(
                () -> new GlobalException(NOT_FOUND_USER.getMessage(), NOT_FOUND_USER.getStatus())
        );
    }
}
