package com.ktb7.pinpung.repository;

import com.ktb7.pinpung.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, String> {
}
