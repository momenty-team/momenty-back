package com.momenty.user.repository;

import com.momenty.user.domain.UserTemporaryStatus;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface UserRedisRepository extends Repository<UserTemporaryStatus, String> {

    UserTemporaryStatus save(UserTemporaryStatus userTemporaryStatus);

    Optional<UserTemporaryStatus> findById(String email);

    void deleteById(String email);
}
