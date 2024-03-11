package com.ImmunTrackApp.ImmunTrackApp.repository;

import com.ImmunTrackApp.ImmunTrackApp.model.ChildEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChildRepo extends JpaRepository<ChildEntity, Integer> {
    Optional<ChildEntity> findByLastname(String lastname);
    boolean existsByLastname(String lastname);
}
