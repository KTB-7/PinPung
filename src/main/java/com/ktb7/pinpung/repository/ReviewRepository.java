package com.ktb7.pinpung.repository;

import com.ktb7.pinpung.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT p.placeId, COUNT(r.reviewId) as reviewCount " +
            "FROM Place p LEFT JOIN Review r ON p.placeId = r.placeId " +
            "WHERE p.placeId IN :placeIds " +
            "GROUP BY p.placeId")
    List<Object[]> findReviewCountsByPlaceIds(@Param("placeIds") List<Long> placeIds);
    List<Review> findByPlaceId(Long placeId);
    List<Review> findByUserIdAndPlaceIdAndReviewId(Long userId, Long placeId, Long reviewId);

    List<Review> findByUserId(Long userId);

    // placeId로 가장 최근 이미지 있는 리뷰의 이미지id 가져오기
    Optional<Review> findTopByPlaceIdOrderByCreatedAtDesc(Long placeId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
}
