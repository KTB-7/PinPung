package com.ktb7.pinpung.repository;

import com.ktb7.pinpung.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPlaceId(String placeId);
}
