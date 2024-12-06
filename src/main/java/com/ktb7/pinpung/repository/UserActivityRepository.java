package com.ktb7.pinpung.repository;

import com.ktb7.pinpung.entity.Token;
import com.ktb7.pinpung.entity.UserActivity;
import com.ktb7.pinpung.entity.UserActivityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, UserActivityId> {
}