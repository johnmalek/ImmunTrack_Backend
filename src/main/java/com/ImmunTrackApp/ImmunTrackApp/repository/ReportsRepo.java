package com.ImmunTrackApp.ImmunTrackApp.repository;

import com.ImmunTrackApp.ImmunTrackApp.model.Reports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportsRepo extends JpaRepository<Reports, Integer> {
}
