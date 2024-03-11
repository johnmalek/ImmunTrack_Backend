package com.ImmunTrackApp.ImmunTrackApp.repository;

import com.ImmunTrackApp.ImmunTrackApp.model.VaccineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VaccineRepo extends JpaRepository<VaccineEntity, Integer> {
    Optional<VaccineEntity> findByVaccineName(String vaccineName);
    boolean existsByVaccineName(String vaccineName);
}
