package com.momenty.user.repository;

import com.momenty.user.domain.Following;
import com.momenty.user.domain.User;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface FollowingRepository extends Repository<Following, Integer> {

    void save(Following following);

    Optional<Following> findByUserAndFollowingUser(User user, User followingUser);
}
