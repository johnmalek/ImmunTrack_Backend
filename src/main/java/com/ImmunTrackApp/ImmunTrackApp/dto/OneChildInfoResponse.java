package com.ImmunTrackApp.ImmunTrackApp.dto;

import com.ImmunTrackApp.ImmunTrackApp.model.ChildEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OneChildInfoResponse {
    private boolean success;
    private String message;
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
