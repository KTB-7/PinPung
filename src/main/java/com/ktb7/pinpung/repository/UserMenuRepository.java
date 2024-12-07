package com.ktb7.pinpung.repository;

import com.ktb7.pinpung.entity.UserMenu;
import com.ktb7.pinpung.entity.UserMenuId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMenuRepository extends JpaRepository<UserMenu, UserMenuId> {
}
