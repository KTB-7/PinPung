package com.ktb7.pinpung.repository;

import com.ktb7.pinpung.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByKakaoPlaceId(String kakaoPlaceId);

    @Query("SELECT p.placeId, p.placeName FROM Place p WHERE p.placeId IN :placeIds")
    List<Object[]> findPlaceNamesByPlaceIds(@Param("placeIds") List<Long> placeIds);
}
