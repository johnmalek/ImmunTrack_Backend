package com.ImmunTrackApp.ImmunTrackApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VaccineDetails {
    private Integer id;
    private String vaccineName;
    private String manufacturer;
    private String batchNo;
    private String expiryDate;
    private String quantity;
}
