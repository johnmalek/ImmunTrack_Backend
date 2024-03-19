package com.ImmunTrackApp.ImmunTrackApp.repository;

import com.ImmunTrackApp.ImmunTrackApp.model.ImmunizationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImmunizationRepo extends JpaRepository<ImmunizationSchedule, Integer> {
}
