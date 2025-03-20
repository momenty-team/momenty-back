package com.momenty.user.repository;

import com.momenty.user.domain.Follower;
import org.springframework.data.repository.Repository;

public interface FollowerRepository extends Repository<Follower, Integer> {

    void save(Follower followerData);
}
