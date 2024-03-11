package com.ImmunTrackApp.ImmunTrackApp.repository;

import com.ImmunTrackApp.ImmunTrackApp.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Integer> {
    Optional<Object> findByEmail(String email);
    boolean existsByEmail(String email);
}
