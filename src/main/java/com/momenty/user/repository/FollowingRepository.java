package com.momenty.user.repository;

import static com.momenty.user.exception.UserExceptionMessage.NOT_FOUND_FOLLOWING_DATA;

import com.momenty.global.exception.GlobalException;
import com.momenty.user.domain.Following;
import com.momenty.user.domain.User;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface FollowingRepository extends Repository<Following, Integer> {

    void save(Following following);

    Optional<Following> findByUserAndFollowingUser(User user, User followingUser);

    default Following getByUserAndFollowingUser(User user, User followingUser) {
        return findByUserAndFollowingUser(user, followingUser).orElseThrow(
                () -> new GlobalException(
                        NOT_FOUND_FOLLOWING_DATA.getMessage(),
                        NOT_FOUND_FOLLOWING_DATA.getStatus()
                )
        );
    }

    void deleteById(Integer id);
}
