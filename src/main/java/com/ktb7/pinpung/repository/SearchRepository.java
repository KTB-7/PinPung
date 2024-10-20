package com.ktb7.pinpung.repository;

import com.ktb7.pinpung.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Place, Long> {
    @Query("SELECT p.placeId, COUNT(r.reviewId) as reviewCount " +
            "FROM Place p LEFT JOIN Review r ON p.placeId = r.placeId " +
            "WHERE p.placeId IN :placeIds " +
            "GROUP BY p.placeId")
    List<Object[]> findReviewCountsByPlaceIds(@Param("placeIds") List<Long> placeIds);
}

