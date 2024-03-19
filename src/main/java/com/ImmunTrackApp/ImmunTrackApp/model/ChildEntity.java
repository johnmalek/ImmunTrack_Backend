package com.ImmunTrackApp.ImmunTrackApp.model;

import jakarta.annotation.security.DenyAll;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "children")
public class ChildEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstname;
    private String lastname;
    private String sex;
    private String mother_name;
    private String mother_id_no;
    private String mother_phone_no;
    private String location;
    private String dob;

    @OneToMany(mappedBy = "child")
    private List<ImmunizationSchedule> immunizationSchedules = new ArrayList<>();
}
