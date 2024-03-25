package com.ImmunTrackApp.ImmunTrackApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllChildrenDetails {
    private Integer id;
    private String firstname;
    private String lastname;
    private String sex;
    private String mother_name;
    private String mother_id_no;
    private String mother_phone_no;
    private String location;
    private String dob;
}
