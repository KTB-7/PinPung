package com.ktb7.pinpung.repository;

import com.ktb7.pinpung.entity.Pung;
import com.ktb7.pinpung.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
}
