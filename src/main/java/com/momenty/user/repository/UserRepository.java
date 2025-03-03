package com.momenty.user.repository;

import com.momenty.global.exception.UserNotFoundException;
import com.momenty.user.domain.User;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface UserRepository extends Repository<User, Integer> {

    User save(User user);

    Optional<User> findById(Integer id);

    default User getById(Integer id) {
        return findById(id).orElseThrow(UserNotFoundException::new);
    }
}
