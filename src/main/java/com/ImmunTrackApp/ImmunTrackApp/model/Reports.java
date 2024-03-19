package com.ImmunTrackApp.ImmunTrackApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reports")
public class Reports {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String child_name;
    private String vaccine_administered;
    private Date date_administered;
    private Date next_dose_date;
    private String compiled_by;
    private String remarks;
}
