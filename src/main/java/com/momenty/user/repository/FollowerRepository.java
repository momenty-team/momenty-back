package com.momenty.user.repository;

import com.momenty.user.domain.Follower;
import com.momenty.user.domain.User;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface FollowerRepository extends Repository<Follower, Integer> {

    void save(Follower followerData);

    Optional<Follower> findByUserAndFollowerUser(User user, User followingUser);
}
