package com.ktb7.pinpung.repository;

import com.ktb7.pinpung.entity.Follow;
import com.ktb7.pinpung.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
}

