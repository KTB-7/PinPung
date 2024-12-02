package com.ktb7.pinpung.repository;

import com.ktb7.pinpung.dto.Profile.SimplePung;
import com.ktb7.pinpung.entity.Pung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PungRepository extends JpaRepository<Pung, Long> {
    Optional<Pung> findFirstByPlaceIdAndIsReviewFalse(Long placeId);
    Page<Pung> findByPlaceIdAndIsReviewFalse(Long placeId, Pageable pageable);

    Page<Pung> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Pung p WHERE p.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT new com.ktb7.pinpung.dto.Profile.SimplePung(p.pungId, p.imageId, p.updatedAt) " +
            "FROM Pung p WHERE p.userId = :userId")
    List<SimplePung> findSimplePungByUserId(@Param("userId") Long userId);

    // pung2review 스케줄러에서 24시간내 펑은 isreview 처리
    @Query("SELECT p FROM Pung p WHERE p.updatedAt >= :yesterday")
    List<Pung> findCreatedIn24H(@Param("yesterday") LocalDateTime yesterday);

}

