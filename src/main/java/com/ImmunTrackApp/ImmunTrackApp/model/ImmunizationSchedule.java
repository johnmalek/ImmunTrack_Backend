package com.ImmunTrackApp.ImmunTrackApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.PrivateKey;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "immunization")
public class ImmunizationSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String vaccine_name;
    private Date scheduled_date;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private ChildEntity child;
}
