package com.ImmunTrackApp.ImmunTrackApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OneVaccineInfoResponse {
    private boolean success;
    private String message;
    private Integer id;
    private String vaccineName;
    private String manufacturer;
    private String batchNo;
    private String expiryDate;
    private String quantity;
}
