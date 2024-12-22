package com.ktb7.pinpung.repository;

import com.ktb7.pinpung.entity.PlaceTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceTagRepository extends JpaRepository<PlaceTag, Long> {

    @Query("SELECT t.tagName " +
            "FROM PlaceTag pt " +
            "JOIN Tag t ON pt.tagId = t.tagId " +
            "GROUP BY t.tagId, t.tagName " +
            "ORDER BY SUM(pt.tagCount) DESC")
    List<String> findTop5TagsByTagCount();

    @Query("SELECT pt.placeId " +
            "FROM PlaceTag pt JOIN Tag t ON pt.tagId = t.tagId " +
            "WHERE t.tagName = :tagName")
    List<Long> findPlaceIdsByTagName(@Param("tagName") String tagName);

}
