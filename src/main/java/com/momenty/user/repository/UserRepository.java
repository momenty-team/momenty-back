package com.momenty.user.repository;

import com.momenty.user.domain.User;
import org.springframework.data.repository.Repository;

public interface UserRepository extends Repository<User, Integer> {

    User save(User user);
}
