package com.ktb7.pinpung.repository;

import com.ktb7.pinpung.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("SELECT p.placeId, t.tagName " +
            "FROM PlaceTag pt JOIN Tag t ON pt.tagId = t.tagId " +
            "JOIN Place p ON pt.placeId = p.placeId " +
            "WHERE p.placeId IN :placeIds")
    List<Object[]> findTagsByPlaceIds(@Param("placeIds") List<Long> placeIds);
}